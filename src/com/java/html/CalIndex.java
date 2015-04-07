package com.java.html;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//计算指标
public class CalIndex {
	// 词个数统计
	int count = 1;
	int docNum;
	// 保存文章分成的所有的簇
	private Map<Integer, ArrayList<Integer>> cluster = new HashMap<Integer, ArrayList<Integer>>();
	// 保存映射关系：索引——>单词
	private Map<Integer, String> indexWord = new HashMap<Integer, String>();
	// 保存映射关系：单词——>索引
	private Map<String, Integer> wordIndex = new HashMap<String, Integer>();
	// 存放单个单词（id）--->的出现的次数
	private Map<Integer, Integer> wordCount = new HashMap<Integer, Integer>();
	// 存放组合单词（id），对应的出现的次数
	private Map<Integer, Map<Integer, Float>> wordAlone = new HashMap<Integer, Map<Integer, Float>>();
	// 该Map存放词的索引 及每个词对应的组合词的索引及相应组合词出现的次数
	private Map<Integer, Map<Long, Integer>> wordMap = new HashMap<Integer, Map<Long, Integer>>();
	/**
	 * 构造函数
	 */
	public CalIndex(String file) throws IOException {
		init_all(file);
		this.docNum = getDocNum(file);
		calAlone(0);
	}

	public CalIndex(String file, String clusterFile) throws IOException {
		init_cluster(clusterFile);
		init_all(file);
		this.docNum = getDocNum(file);
		calAlone(0);
		calCluserClose(file);
	}

	/**
	 * 未分簇时，得到文章的数目
	 * 
	 */
	private int getDocNum(String file) throws IOException {
		RandomAccessFile rand = new RandomAccessFile(file, "r");
		int docNum;
		long pos = rand.length() - 2;
		String line = "";
		String temp[];
		while (pos >= 0) {
			rand.seek(pos);// 将指针移到相应的位置，才能读取。
			if (rand.readByte() == '\n') {
				line = rand.readLine();
				break;
			}
			pos--;
		}
		if (line != "") {
			temp = line.split("	", 2);
			docNum = Integer.parseInt(temp[0]);
		} else
			docNum = 1;
		return docNum;
	}

	/**
	 * 初始化 每个簇
	 * 
	 * @throws IOException
	 */
	public void init_cluster(String clusterFile) throws IOException {
		BufferedReader read = new BufferedReader(new FileReader(clusterFile));
		String line = "";
		String a[] = null;
		int docID, cluCen;
		while ((line = read.readLine()) != null) {
			a = line.split("	");
			cluCen = Integer.parseInt(a[1]);
			docID = Integer.parseInt(a[0]);
			cluster(cluCen, docID);
		}
		read.close();
		int sum = 0;
		for (Iterator<Integer> it = cluster.keySet().iterator(); it.hasNext();) {
			int clu = it.next();
			sum += cluster.get(clu).size();
		}
		System.out.println(sum);
	}

	/**
	 * 初始化簇map
	 * 
	 */
	private void cluster(int cluCen, int docID) {
		if (!cluster.containsKey(cluCen)) {
			ArrayList<Integer> temp = new ArrayList<Integer>();
			cluster.put(cluCen, temp);
		}
		List<Integer> clu1 = cluster.get(cluCen);
		clu1.add(docID);

	}

	/**
	 * 计算每个簇 的相邻词紧密度
	 * 
	 * @throws IOException
	 */
	private void calCluserClose(String file) throws IOException {
		for (Iterator<Integer> it = cluster.keySet().iterator(); it.hasNext();) {
			int clu = it.next();
			restoreMap();
			List<Integer> temp = cluster.get(clu);
			this.docNum = temp.size();
			Collections.sort(temp);
			init_clusterWord(file, temp);
			calAlone(clu);
		}

	}

	/**
	 * 还原统计map的值
	 */
	private void restoreMap() {
		wordCount.clear();
		wordMap.clear();
	}

	/**
	 * 进行各个成员域的初始化(在处理每个簇词出现的次数也要初始化这些成员域) //处理未为分簇的情况
	 */
	private void init_all(String file) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(new File(file)));
		String line = "";
		String[] row = null;
		String[] words = null;
		while ((line = in.readLine()) != null) {
			row = line.split("	", 2);
			words = row[1].split(" ");
			init_part_all(words);
		}
		in.close();
	}

	/**
	 * 读取每个簇中的文章
	 * 
	 */
	private void init_clusterWord(String file, List<Integer> clu)
			throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(new File(file)));
		String line = "";
		String[] row = null;
		String[] words = null;
		int id = 0, i = 0;
		while ((line = in.readLine()) != null) {
			if (++id == clu.get(i)) {
				row = line.split("	", 2);
				words = row[1].split(" ");
				init_part_clu(words);
				i++;
				if (i == clu.size())
					break;
			}
		}
		in.close();
	}

	private void init_part_clu(String[] words) {
		for (int i = 0; i < words.length; i++) {
			if (!words[i].matches("(\\pP)|(\\s)")) {
				int index = wordIndex.get(words[i]);
				Map<Long, Integer> mulIndexCount = null;
				if (!wordMap.containsKey(index)) {
					mulIndexCount = new HashMap<Long, Integer>();
					wordCount.put(index, 1);
				} else {
					int oldCount = wordCount.get(index);
					wordCount.put(index, ++oldCount);
					mulIndexCount = wordMap.get(index);
				}

				// 下个单词不是标点符号
				if (i + 1 < words.length && !words[i + 1].matches("\\pP")) {
					long Lindex = wordIndex.get(words[i]);
					long Rindex = wordIndex.get(words[i + 1]);
					long mulKey = Lindex * 100000 + Rindex;
					if (!mulIndexCount.containsKey(mulKey)) {
						mulIndexCount.put(mulKey, 1);
					} else {
						int oldCount = mulIndexCount.get(mulKey);
						mulIndexCount.put(mulKey, ++oldCount);
					}
					wordMap.put(index, mulIndexCount);

					Map<Long, Integer> mulIndexCount2 = null;
					int indexNext = wordIndex.get(words[i + 1]);
					if (!wordMap.containsKey(indexNext)) {
						mulIndexCount2 = new HashMap<Long, Integer>();
						wordCount.put(indexNext, 0);
					} else {
						mulIndexCount2 = wordMap.get(indexNext);
					}

					if (!mulIndexCount2.containsKey(mulKey)) {
						mulIndexCount2.put(mulKey, 1);
					} else {

						int oldCount = mulIndexCount2.get(mulKey);
						mulIndexCount2.put(mulKey, ++oldCount);
					}
					wordMap.put(indexNext, mulIndexCount2);
				}
			} 
		}
	}

	/**
	 * 对一篇文章进行统计，单个词及相邻词出现的次数
	 */
	private void init_part_all(String[] words) {
		for (int i = 0; i < words.length; i++) {
			if (!words[i].matches("(\\pP)|(\\s)")) {
				if (!wordIndex.containsKey(words[i])) {
					wordIndex.put(words[i], count);
					indexWord.put(count++, words[i]);
				}
				int index = wordIndex.get(words[i]);
				Map<Long, Integer> mulIndexCount = null;
				if (!wordMap.containsKey(index)) {
					mulIndexCount = new HashMap<Long, Integer>();
					wordCount.put(index, 1);
				} else {
					int oldCount = wordCount.get(index);
					wordCount.put(index, ++oldCount);
					mulIndexCount = wordMap.get(index);
				}
				// 下个单词不是标点符号
				if (i + 1 < words.length && !words[i + 1].matches("(\\pP)|(\\s)")) {
					if (!wordIndex.containsKey(words[i + 1])) {
						wordIndex.put(words[i + 1], count);
						indexWord.put(count++, words[i + 1]);
					}
					long Lindex = wordIndex.get(words[i]);
					long Rindex = wordIndex.get(words[i + 1]);
					long mulKey = Lindex * 100000 + Rindex;
					if (!mulIndexCount.containsKey(mulKey)) {
						mulIndexCount.put(mulKey, 1);
					} else {
						int oldCount = mulIndexCount.get(mulKey);
						mulIndexCount.put(mulKey, ++oldCount);
					}
					wordMap.put(index, mulIndexCount);

					Map<Long, Integer> mulIndexCount2 = null;
					int indexNext = wordIndex.get(words[i + 1]);
					if (!wordMap.containsKey(indexNext)) {
						mulIndexCount2 = new HashMap<Long, Integer>();
						wordCount.put(indexNext, 0);
					} else {
						mulIndexCount2 = wordMap.get(indexNext);
					}

					if (!mulIndexCount2.containsKey(mulKey)) {
						mulIndexCount2.put(mulKey, 1);
					} else {

						int oldCount = mulIndexCount2.get(mulKey);
						mulIndexCount2.put(mulKey, ++oldCount);
					}
					wordMap.put(indexNext, mulIndexCount2);
				}

			} 
		}
	}

	/**
	 * 计算组合词在 整篇文章 中的紧密度
	 */
	public void calAlone(int cen) {
		Iterator<Integer> it = wordMap.keySet().iterator();
		while (it.hasNext()) {
			int index = it.next();
			double pa = (wordCount.get(index) * 1.0) / docNum;
			Map<Long, Integer> mulIndexAndCount = wordMap.get(index);
			Iterator<Long> mul = mulIndexAndCount.keySet().iterator();
			int sum = 0;
			while (mul.hasNext()) {
				Long indexTemp = mul.next();
				sum += mulIndexAndCount.get(indexTemp);
			}

			double temp = pa - (sum * 1.0 / docNum / wordMap.get(index).size());
			float alone = (float) Math.log(temp);
			if (cen == 0) {
				Map<Integer, Float> map = new HashMap<Integer, Float>();
				map.put(0, alone);
				wordAlone.put(index, map);
			} else {
				Map<Integer, Float> map = wordAlone.get(index);
				map.put(cen, alone);
			}
		}
	}

	public void printAlone(String file) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		Map<Integer, Float> alone = null;
		for (Iterator<Integer> it = wordAlone.keySet().iterator(); it.hasNext();) {
			Integer index = it.next();
			String word = indexWord.get(index);
			alone = wordAlone.get(index);
			out.write(index + "	" + word + "	");

			for (Iterator<Integer> is = alone.keySet().iterator(); is.hasNext();) {
				int clu = is.next();
				out.write(clu + ":" + Float.toString(alone.get(clu)) + "	");
			}
			out.newLine();
			out.flush();
		}
		out.close();
	}

	public static void main(String[] args) throws IOException {
		CalIndex cal = new CalIndex("D:\\article2.txt", "D:\\ss3.txt");
		cal.printAlone("D:\\alone.txt");
		System.out.println("结束");
	}
}
