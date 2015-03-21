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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * 相似度对象
 */
class Simily {
	private float cos[][];
	int docNum;

	public Simily(int docNum) {
		this.docNum = docNum;
		cos = new float[docNum + 1][];
		for (int i = 1; i <= docNum; i++) {
			cos[i] = new float[docNum - i];// 数组下标j对应的真实文章id为i+j+1；
		}
	}

	/**
	 * 得到指定下标的相似度
	 */
	public float getEle(int i, int j) {
		return cos[i][j];
	}

	/**
	 * 打印相似度到 file文件
	 */
	public void printSim(String file) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(new File(file)));
		for (int i = 1; i <= docNum; i++) {
			out.write(i + "");
			for (int j = i + 1; j <= docNum; j++) {
				if (cos[i][j - i - 1] != 0) {
					out.write(" ");
					out.write(j + ":" + Math.round(cos[i][j - i - 1] * 10000)
							/ 10000.0);
				}
			}
			out.newLine();
			out.flush();
		}
		out.close();
	}

	/**
	 * 得到某一维的长度
	 */
	public int getLen(int i) {
		return cos[i].length;
	}

	/**
	 * 更新下标值为 i，j的值
	 */
	public void update(int i, int j, float value) {
		cos[i][j] += value;
	}

	public void readSimFile(String file) throws IOException {
		BufferedReader r = new BufferedReader(new FileReader(file));
		String line = "";
		int count = 1;
		while ((line = r.readLine()) != null) {
			String[] row = line.split("	");
			if (!row[0].matches("\\d+"))// 检查行号
			{
				System.out.println("您输入的格式有问题，在第" + count + "行！");
				System.exit(1);
			}
			int rowID = Integer.parseInt(row[0]);
			for (int i = 1; i < row.length; i++) {
				String ele[] = row[i].split(":");
				// 检查文件号及其相似度
				int columID=0;
				float sim=0;
				if ((!ele[0].matches("\\d+") || (columID=Integer.parseInt(ele[0])) <= 0)
						&& (!ele[1].matches("(\\d)|(\\d\\.\\d+)") || ((sim=Float
								.parseFloat(ele[1])) < 0 || sim > 1))) {
					System.out.println("您输入的格式有问题，在第" + count + "行！");
					System.exit(1);
				}
				if(rowID<columID)
				    cos[rowID][columID - rowID - 1] = sim;
				else
					cos[columID][rowID - columID - 1] = sim;
			}
			count++;
		}
		r.close();
	}
}

class Cluster {
	public int center;
	List<Integer> child = new ArrayList<Integer>();

	public Cluster(int cen) {
		center = cen;
		child.add(center);
	}

	/**
	 * 为该聚类添加孩子
	 */
	public void add(int child) {
		this.child.add(child);
	}

	public void printClu() {
		System.out.println("聚类中心： " + center);
		System.out.print("包含的孩子：");
		for (int i = 0; i < child.size(); i++)
			System.out.print(child.get(i) + " ");
		System.out.println();
	}

	/**
	 * 判断某个文章是否在此聚类中
	 */
	public boolean isIn(int doc) {
		if (child.contains(doc))
			return true;
		return false;
	}
}

class DocArgs {
	public int id;// 文章编号
	public int p;// 文章的密度
	public float q = 1;// 密度比此文章大 且距离最小的距离
	public int qID;// 产生最小距离的文章的ID
	public boolean hasClu = false;// 标志位该文章是否已经有归属,聚类中心的初始值为true
	public boolean isCen = false;// 判断是否为聚类中心
	// 存放孩子 构造以聚类中心的森林 方便寻找 该聚类包含的所有文章
	List<Integer> child = new LinkedList<Integer>();

	public DocArgs(int id) {
		this.id = id;
	}
}

public class FastCluster {
	// 文章数
	int docNum;
	// 存放两两文章相似度（非零元素上三角）
	Simily cos = null;
	// 存放文章的属性
	DocArgs[] doc;
	// 用于存放生成的簇
	List<Cluster> clu = new ArrayList<Cluster>();

	public FastCluster(String file) throws IOException {
		System.out.println("开始读入相似度文件……");
		getDocNum(file);
		cos = new Simily(docNum);
		readSim(file);
		System.out.println("相似度文件已经读入……");
	}

	/**
	 * 得到文章数量
	 * 
	 */
	private void getDocNum(String file) throws IOException {
		long len = 0;
		String line = "";
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(file, "r");
			len = raf.length();
			long pos = len - 2;
			while (pos >= 0) {
				raf.seek(pos);
				if (raf.readByte() == '\n') {
					line = raf.readLine();
					;
					break;
				}
				pos--;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String a[] = line.split("\t", 2);
		this.docNum = Integer.parseInt(a[0]);
	}

	/**
	 * 输出相似度到磁盘
	 * 
	 */
	public void printCOS(String file) throws IOException {
		cos.printSim(file);
	}

	public void readSim(String file) throws IOException {
		cos.readSimFile(file);
	}

	/**
	 * 得到聚类的中心 d为指定的两两文章的距离
	 * 
	 * @throws IOException
	 */
	public void cluster(float d, float pq, String pqFile) throws IOException {
		initDoc();
		System.out.println("开始获得每个文章的密度P……");
		getP(d);
		System.out.println("开始获得每个文章的q……");
		getQ();
		System.out.println("寻找聚类中心……");
		getCenter(pq);
		System.out.println("对文章进行分类……");
		divideDoc();// 分类
		finalClu();// 为每一个中心分配文章
		System.out.println("分类完毕……");
		System.out.println("开始打印pq文件……");
		printPQ(pqFile);
		System.out.println("pq文件打印结束……");
	}

	private void printPQ(String pqFile) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(pqFile));
		for (int i = 1; i <= docNum; i++) {
			out.write(doc[i].id + "	" + doc[i].p  + "	" + doc[i].q + "	"
					+ doc[i].q * doc[i].p);
			out.newLine();
		}
		out.close();
	}

	/**
	 * 从控制台输出 各个聚类及其所包含的文件
	 */
	public void printClu() {
		for (int i = 0; i < clu.size(); i++) {
			clu.get(i).printClu();
		}
	}

	public void printFile(String file) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		for (int i = 1; i <= docNum; i++) {
			int cen = 0;
			for (int j = 0; j < clu.size(); j++)
				if (clu.get(j).isIn(i)) {
					cen = clu.get(j).center;
					break;
				}
			out.write(i + "	" + cen);
			out.newLine();
			out.flush();
		}
		out.close();
	}

	/**
	 * 对doc初始化
	 */
	private void initDoc() {
		doc = new DocArgs[docNum + 1];
		for (int i = 0; i <= docNum; i++)
			doc[i] = new DocArgs(i);
		doc[0].p = -100;
	}

	/**
	 * 找到聚类的中心点,初始化每个聚类的簇
	 * 
	 */
	public void getCenter(float pq) {
		for (int i = 1; i <= docNum; i++) {
			if (doc[i].p * doc[i].q >= pq) {
				doc[i].isCen = true;
				doc[i].hasClu = true;
				clu.add(new Cluster(doc[i].id));
			}
		}
	}

	/**
	 * 得到聚类的密度
	 */
	private void getP(float d) {
		for (int doc1 = 1; doc1 < docNum; doc1++) {
			for (int j = 0; j < cos.getLen(doc1); j++) {
				int doc2 = j + doc1 + 1;
				float cosn = cos.getEle(doc1, j);
				if (cosn >= d) {
					doc[doc1].id = doc1;
					doc[doc1].p++;
					doc[doc2].id = doc2;
					doc[doc2].p++;
				}
			}
		}
	}

	/**
	 * 为了减少时间要对所有的文章按密度递增的顺序排序；
	 */
	private void pSort() {
		Arrays.sort(doc, new Comparator<DocArgs>() {

			@Override
			public int compare(DocArgs o1, DocArgs o2) {
				if (o1.p > o2.p)
					return 1;
				else if (o1.p < o2.p)
					return -1;
				else
					return 0;
			}
		});
	}

	/**
	 * 为了在寻找聚类中心递归运算时候，方便查找要对所有的文章按id递增的顺序排序；
	 */
	private void IDSort() {
		Arrays.sort(doc, new Comparator<DocArgs>() {

			@Override
			public int compare(DocArgs o1, DocArgs o2) {
				if (o1.id > o2.id)
					return 1;
				else if (o1.id < o2.id)
					return -1;
				else
					return 0;
			}
		});
	}

	/**
	 * 得到文章的第二个属性q：比此文章大的，且距离最近的距离
	 */
	private void getQ() {
		pSort();
		// for(int i=0;i<=docNum;i++)
		// System.out.println(doc[i].id+" "+doc[i].p+" "+doc[i].q+" "+doc[i].qID);
		for (int i = 1; i <= docNum; i++) {
			int j = i + 1;
			// 排除密度和它相等的文章
			while (j <= docNum && doc[i].p == doc[j].p)
				j++;
			for (; j <= docNum; j++) {
				int min = doc[i].id > doc[j].id ? doc[j].id : doc[i].id;
				int max = doc[i].id < doc[j].id ? doc[j].id : doc[i].id;
				if (doc[i].q > 1 - cos.getEle(min, max - min - 1))// 更新距离
				{
					doc[i].q = 1 - cos.getEle(min, max - min - 1);
					doc[i].qID = doc[j].id;
				}
			}
		}
	}

	/**
	 * 为每个聚类中心分配 文章 递归算法
	 */
	private void divideDoc() {
		IDSort();
		// for(int i=0;i<=docNum;i++)
		// System.out.println(doc[i].id+" "+doc[i].p+" "+doc[i].q+" "+doc[i].qID);
		for (int i = 1; i <= docNum; i++)
			if (doc[i].hasClu == false)
				divide(i);
	}

	private void divide(int id) {
		if (doc[id].hasClu == false) {
			doc[id].hasClu = true;
			doc[doc[id].qID].child.add(id);
			divide(doc[id].qID);
		}
	}

	/**
	 * 递归得到cen聚类中心拥有的文章
	 */
	public void finalClu() {
		for (int i = 0; i < clu.size(); i++) {
			Cluster cluster = clu.get(i);
			int cen = cluster.center;
			getSubCluster(cen, cluster.child);
		}
	}

	public void getSubCluster(int cen, List<Integer> clu) {
		for (int i = 0; i < doc[cen].child.size(); i++) {
			int center = doc[cen].child.get(i);
			clu.add(center);
			getSubCluster(center, clu);
		}
	}

	private static boolean checkArgs(String args[]) {

		if (args.length != 5) {
			System.out.println("您输入的参数个数不正确，请确保是5个！！");
			return false;
		}
		if (!new File(args[0]).exists()) {
			System.out.println("您输入文件不存在，请仔细检查！！");
			return false;
		}
		if (!(args[3].matches("(\\d)|(\\d\\.\\d+)"))
				|| (Float.parseFloat(args[3]) < 0 || Float.parseFloat(args[3]) > 1)) {
			System.out.println("您输入参数d不合法，请仔细检查！！");
			return false;
		}
		if (!(args[4].matches("(\\d+\\.\\d+)|(\\d+)"))
				|| (Float.parseFloat(args[4]) < 0)) {
			System.out.println("您输入参数pq不合法，请仔细检查！！");
			return false;
		}
		return true;
	}

	/*
	 * args数组的参数：args[0]:为相似度文件路径 args[1]:为pq文件的输出路径 args[2]:为聚类文件的输出路径
	 * args[3]:为参数为相似度 args[4]:为参数pq
	 */
	public static void main(String[] args) throws IOException {
		System.out.println("开始运行……");
		System.out.println("正在检查参数……");
		if (!checkArgs(args))
			System.exit(1);
		System.out.println("参数正确……");
		FastCluster fc = new FastCluster(args[0]);
		fc.cluster(Float.parseFloat(args[3]), Float.parseFloat(args[4]),
				args[1]);
		System.out.println("开始打印聚类文件……");
		fc.printFile(args[2]);
		System.out.println("运行结束……");
		// D:\\世界杯\\sim.txt
		// D:\\世界杯\\cosqq.txt
		// D:\\世界杯\\qq.txt
	}
}
