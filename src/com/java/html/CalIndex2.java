package com.java.html;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// ���������ָ��
public class CalIndex2 {
	// �ʸ���ͳ��
	int count = 1;
	int docNum;

	// ����ӳ���ϵ����������>����
	private Map<Integer, String> indexWord = new HashMap<Integer, String>();
	// ����ӳ���ϵ�����ʡ���>����
	private Map<String, Integer> wordIndex = new HashMap<String, Integer>();
	// ÿ���ʳ��ֵĴ���
	private Map<Integer, Integer> wordCount = new HashMap<Integer, Integer>();
	// ��Map��Ŵʵ����� ��ÿ���ʶ�Ӧ����ϴʵ���������Ӧ��ϴʳ��ֵĴ���
	private Map<Integer, Map<Long, Integer>> wordMap = new HashMap<Integer, Map<Long, Integer>>();
	// ���ʵĶ�����
	private Map<Integer, Float> wordAlone = new HashMap<Integer, Float>();

	/**
	 * ���캯��
	 */
	public CalIndex2(String file) throws IOException{
		   init_all(file);
		   this.docNum=getDocNum(file);
	   }
	public CalIndex2(String file,int []cluster) throws IOException
	{
		init_cluster(file,cluster);
		this.docNum=cluster.length;
	}
	   /**
	   * δ�ִ�ʱ���õ����µ���Ŀ
	   * 
	   */
	private int getDocNum(String file) throws IOException {
		RandomAccessFile rand=new RandomAccessFile(file,"r" );
		int docNum;
		long pos=rand.length()-2;
		String line="";
		String temp[];
		while(pos>=0)
		{
			rand.seek(pos);//��ָ���Ƶ���Ӧ��λ�ã����ܶ�ȡ��
			if(rand.readByte() =='\n')
			{
				line=rand.readLine();
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
	   * ���и�����Ա��ĳ�ʼ��
	   * 
	   */ //����δΪ�ִص����
	private void  init_all(String file) throws IOException
	   {
		   BufferedReader in=new BufferedReader(new  FileReader(new File(file)));
		   String line="";
		   String []row=null;
		   String[]words=null;
		   while((line=in.readLine())!=null)
		   {
			   row=line.split("	", 2);
			   words=row[1].split(" ");
			   init_part(words);
		   }
		   in.close();
	   }
	private void  init_cluster(String file,int []cluster) throws IOException
	{
		BufferedReader in=new BufferedReader(new  FileReader(new File(file)));
		   String line="";
		   String []row=null;
		   String[]words=null;
		   int count=0,i=0;
		   while((line=in.readLine())!=null)
		   {
		       if(++count==cluster[i])
			   { 
		    	   row=line.split("	", 2);
			       words=row[1].split(" ");
			       init_part(words);
			       i++;
			       if(i>=cluster.length)
			    	   break;
			   }
		    }
		   in.close();
	}
	/**
	 * ��һƪ���½���ͳ�ƣ������ʼ����ڴʳ��ֵĴ���
	 */
	private void init_part(String[] words) {
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
				// �¸����ʲ��Ǳ�����
				if (i + 1 < words.length
						&& !words[i + 1].matches("(\\pP)|(\\s)")) {
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
	 * ����ÿ���ʵĶ�����
	 */
	public void calAlone() {
		Iterator<Integer> it = wordMap.keySet().iterator();
		while (it.hasNext()) {
			int index = it.next();
			double pa = (wordCount.get(index) * 1.0) / docNum;
			Map<Long, Integer> mulIndexAndCount = wordMap.get(index);
			Iterator<Long> mul = mulIndexAndCount.keySet().iterator();
			int sum = 0;
			//String word = indexWord.get(index);
			//System.out.print(word+"  :");
			while (mul.hasNext()) {
				Long indexTemp = mul.next();
				sum += mulIndexAndCount.get(indexTemp);
				/*int left = (int)(indexTemp/100000);
				int right = (int)(indexTemp%100000);
				String leftStr = indexWord.get(left);
				String rightStr = indexWord.get(right);
				System.out.print(leftStr + " "+rightStr+"	");*/
			}
			//System.out.println();
			double temp = pa - (sum * 1.0 / docNum / wordMap.get(index).size());
			//System.out.println(sum + "	"+wordCount.get(index)+" 	"+temp);
			
			float alone = (float) Math.log(temp);
			wordAlone.put(index, alone);
		}
	}

	public void printAlone(String file) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		Iterator<Integer> it = wordAlone.keySet().iterator();
		while (it.hasNext()) {
			int index = it.next();
			String word = indexWord.get(index);
			float alone = wordAlone.get(index);
			out.write(index + "	" + word + "	");
			out.write(alone + "");
			out.newLine();
			out.flush();
		}
		out.close();
	}

	public static void main(String[] args) throws IOException {
		CalIndex2 cal = new CalIndex2("D:\\article2.txt");
		cal.calAlone();
		cal.printAlone("D:\\alone.txt");
		System.out.println("����");
	}
}
