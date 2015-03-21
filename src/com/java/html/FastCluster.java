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
 * ���ƶȶ���
 */
class Simily {
	private float cos[][];
	int docNum;

	public Simily(int docNum) {
		this.docNum = docNum;
		cos = new float[docNum + 1][];
		for (int i = 1; i <= docNum; i++) {
			cos[i] = new float[docNum - i];// �����±�j��Ӧ����ʵ����idΪi+j+1��
		}
	}

	/**
	 * �õ�ָ���±�����ƶ�
	 */
	public float getEle(int i, int j) {
		return cos[i][j];
	}

	/**
	 * ��ӡ���ƶȵ� file�ļ�
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
	 * �õ�ĳһά�ĳ���
	 */
	public int getLen(int i) {
		return cos[i].length;
	}

	/**
	 * �����±�ֵΪ i��j��ֵ
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
			if (!row[0].matches("\\d+"))// ����к�
			{
				System.out.println("������ĸ�ʽ�����⣬�ڵ�" + count + "�У�");
				System.exit(1);
			}
			int rowID = Integer.parseInt(row[0]);
			for (int i = 1; i < row.length; i++) {
				String ele[] = row[i].split(":");
				// ����ļ��ż������ƶ�
				int columID=0;
				float sim=0;
				if ((!ele[0].matches("\\d+") || (columID=Integer.parseInt(ele[0])) <= 0)
						&& (!ele[1].matches("(\\d)|(\\d\\.\\d+)") || ((sim=Float
								.parseFloat(ele[1])) < 0 || sim > 1))) {
					System.out.println("������ĸ�ʽ�����⣬�ڵ�" + count + "�У�");
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
	 * Ϊ�þ�����Ӻ���
	 */
	public void add(int child) {
		this.child.add(child);
	}

	public void printClu() {
		System.out.println("�������ģ� " + center);
		System.out.print("�����ĺ��ӣ�");
		for (int i = 0; i < child.size(); i++)
			System.out.print(child.get(i) + " ");
		System.out.println();
	}

	/**
	 * �ж�ĳ�������Ƿ��ڴ˾�����
	 */
	public boolean isIn(int doc) {
		if (child.contains(doc))
			return true;
		return false;
	}
}

class DocArgs {
	public int id;// ���±��
	public int p;// ���µ��ܶ�
	public float q = 1;// �ܶȱȴ����´� �Ҿ�����С�ľ���
	public int qID;// ������С��������µ�ID
	public boolean hasClu = false;// ��־λ�������Ƿ��Ѿ��й���,�������ĵĳ�ʼֵΪtrue
	public boolean isCen = false;// �ж��Ƿ�Ϊ��������
	// ��ź��� �����Ծ������ĵ�ɭ�� ����Ѱ�� �þ����������������
	List<Integer> child = new LinkedList<Integer>();

	public DocArgs(int id) {
		this.id = id;
	}
}

public class FastCluster {
	// ������
	int docNum;
	// ��������������ƶȣ�����Ԫ�������ǣ�
	Simily cos = null;
	// ������µ�����
	DocArgs[] doc;
	// ���ڴ�����ɵĴ�
	List<Cluster> clu = new ArrayList<Cluster>();

	public FastCluster(String file) throws IOException {
		System.out.println("��ʼ�������ƶ��ļ�����");
		getDocNum(file);
		cos = new Simily(docNum);
		readSim(file);
		System.out.println("���ƶ��ļ��Ѿ����롭��");
	}

	/**
	 * �õ���������
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
	 * ������ƶȵ�����
	 * 
	 */
	public void printCOS(String file) throws IOException {
		cos.printSim(file);
	}

	public void readSim(String file) throws IOException {
		cos.readSimFile(file);
	}

	/**
	 * �õ���������� dΪָ�����������µľ���
	 * 
	 * @throws IOException
	 */
	public void cluster(float d, float pq, String pqFile) throws IOException {
		initDoc();
		System.out.println("��ʼ���ÿ�����µ��ܶ�P����");
		getP(d);
		System.out.println("��ʼ���ÿ�����µ�q����");
		getQ();
		System.out.println("Ѱ�Ҿ������ġ���");
		getCenter(pq);
		System.out.println("�����½��з��࡭��");
		divideDoc();// ����
		finalClu();// Ϊÿһ�����ķ�������
		System.out.println("������ϡ���");
		System.out.println("��ʼ��ӡpq�ļ�����");
		printPQ(pqFile);
		System.out.println("pq�ļ���ӡ��������");
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
	 * �ӿ���̨��� �������༰�����������ļ�
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
	 * ��doc��ʼ��
	 */
	private void initDoc() {
		doc = new DocArgs[docNum + 1];
		for (int i = 0; i <= docNum; i++)
			doc[i] = new DocArgs(i);
		doc[0].p = -100;
	}

	/**
	 * �ҵ���������ĵ�,��ʼ��ÿ������Ĵ�
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
	 * �õ�������ܶ�
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
	 * Ϊ�˼���ʱ��Ҫ�����е����°��ܶȵ�����˳������
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
	 * Ϊ����Ѱ�Ҿ������ĵݹ�����ʱ�򣬷������Ҫ�����е����°�id������˳������
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
	 * �õ����µĵڶ�������q���ȴ����´�ģ��Ҿ�������ľ���
	 */
	private void getQ() {
		pSort();
		// for(int i=0;i<=docNum;i++)
		// System.out.println(doc[i].id+" "+doc[i].p+" "+doc[i].q+" "+doc[i].qID);
		for (int i = 1; i <= docNum; i++) {
			int j = i + 1;
			// �ų��ܶȺ�����ȵ�����
			while (j <= docNum && doc[i].p == doc[j].p)
				j++;
			for (; j <= docNum; j++) {
				int min = doc[i].id > doc[j].id ? doc[j].id : doc[i].id;
				int max = doc[i].id < doc[j].id ? doc[j].id : doc[i].id;
				if (doc[i].q > 1 - cos.getEle(min, max - min - 1))// ���¾���
				{
					doc[i].q = 1 - cos.getEle(min, max - min - 1);
					doc[i].qID = doc[j].id;
				}
			}
		}
	}

	/**
	 * Ϊÿ���������ķ��� ���� �ݹ��㷨
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
	 * �ݹ�õ�cen��������ӵ�е�����
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
			System.out.println("������Ĳ�����������ȷ����ȷ����5������");
			return false;
		}
		if (!new File(args[0]).exists()) {
			System.out.println("�������ļ������ڣ�����ϸ��飡��");
			return false;
		}
		if (!(args[3].matches("(\\d)|(\\d\\.\\d+)"))
				|| (Float.parseFloat(args[3]) < 0 || Float.parseFloat(args[3]) > 1)) {
			System.out.println("���������d���Ϸ�������ϸ��飡��");
			return false;
		}
		if (!(args[4].matches("(\\d+\\.\\d+)|(\\d+)"))
				|| (Float.parseFloat(args[4]) < 0)) {
			System.out.println("���������pq���Ϸ�������ϸ��飡��");
			return false;
		}
		return true;
	}

	/*
	 * args����Ĳ�����args[0]:Ϊ���ƶ��ļ�·�� args[1]:Ϊpq�ļ������·�� args[2]:Ϊ�����ļ������·��
	 * args[3]:Ϊ����Ϊ���ƶ� args[4]:Ϊ����pq
	 */
	public static void main(String[] args) throws IOException {
		System.out.println("��ʼ���С���");
		System.out.println("���ڼ���������");
		if (!checkArgs(args))
			System.exit(1);
		System.out.println("������ȷ����");
		FastCluster fc = new FastCluster(args[0]);
		fc.cluster(Float.parseFloat(args[3]), Float.parseFloat(args[4]),
				args[1]);
		System.out.println("��ʼ��ӡ�����ļ�����");
		fc.printFile(args[2]);
		System.out.println("���н�������");
		// D:\\���籭\\sim.txt
		// D:\\���籭\\cosqq.txt
		// D:\\���籭\\qq.txt
	}
}
