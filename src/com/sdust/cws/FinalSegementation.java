package com.sdust.cws;

import ICTCLAS.I3S.AC.ICTCLAS50;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ICTCLAS.I3S.AC.ICTCLAS50;

public class FinalSegementation {
	private static ICTCLAS50 test = new ICTCLAS50();
	/*
	 * 
	 * ��ʼ��ICTCLASS�ִ�ϵͳ
	 * */
	static {
		try {
			String sPath = ".";
			if (test.ICTCLAS_Init(sPath.getBytes("GB2312")) == false) {
				System.out.println("��ʼ��ʧ��");
			} else
				System.out.println("��ʼ���ɹ�");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * 
	 * �˳��ִ�
	 * */
	public void exit() {
		test.ICTCLAS_Exit();
	}

	/*
	 * 
	 * �õ��ı��ļ�����
	 * */
	public String getFileContent(String filePath) throws Exception {
		String temp;
		StringBuffer sb = new StringBuffer();
		BufferedReader in = new BufferedReader(new FileReader(
				new File(filePath)));
		while ((temp = in.readLine()) != null) {
			sb.append(temp);
		}
		in.close();
		return sb.toString();
	}

	/*
	 * 
	 * ���ļ���ÿ�����ҵ���Ϣ��Ӳ�����
	 * */
	public Map<String, List<String>> getAllInformation(String filePath)
			throws Exception {// filePathΪ��Ź���txt���ļ�
		Map<String, List<String>> map = new LinkedHashMap<String, List<String>>();
		// System.out.println("��ȥ��");
		File file = new File(filePath);
		String[] list = file.list();
		for (String temp : list) {
			String CountryKey = temp.substring(0, temp.indexOf('.'));
			String CountryValue = getFileContent(filePath + "/" + temp);
			String valueArry[] = CountryValue.split(" ");
			List<String> tempList = new ArrayList<String>();
			for (int i = 0; i < valueArry.length; i++) {
				tempList.add(valueArry[i]);
			}
			map.put(CountryKey, tempList);
		}
		return map;
	}

	/*
	 * 
	 * ���ַ������зִ�,���е����Զ���Ĵʵ�
	 * */
	public String segementWord(String input) {
		String result = "";
		try {

			int count;
			// �����û��ʵ�
			String user = "userdic.txt";
			count = test.ICTCLAS_ImportUserDictFile(user.getBytes(), 2);
			// System.out.println(count);
			byte[] resultByte = test.ICTCLAS_ParagraphProcess(
					input.getBytes("GB2312"), 0, 0);
			result = new String(resultByte, 0, resultByte.length, "GB2312");
			// test.ICTCLAS_Exit();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/*
	 * 
	 * 
	 *  �õ��ִʽ�����Ҵ���Ӳ��
	 * */
	public void getSegeResult() throws IOException {
		File fileFolder = new File("D:\\���籭\\��������");
		String[] a = fileFolder.list();
		for (int j = 0; j < a.length; j++) {
			File fileResult = new File("D:\\���籭\\�������ķִ�\\�ִ�" + a[j]);
			BufferedWriter out = new BufferedWriter(new FileWriter(fileResult));
			File file = new File("D:\\���籭\\��������/" + a[j]);
			BufferedReader in = new BufferedReader(new FileReader(file));
			String temp;
			int i = 1;
			outer: while ((temp = in.readLine()) != null) {
				if (i % 3 == 1) {
					out.write(temp);
					out.newLine();
					i++;
					continue outer;
				}
				out.write(segementWord(temp));
				out.newLine();
				i++;
			}

			in.close();
			out.close();
		}

		exit();

	}

	/*
	 * 
	 * 
	 * /�����Լ����ܵĴʵ�
	 * */
	public void CreateDic(Map<String, List<String>> map) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(new File(
				"D:/���籭/�ܴʵ�.txt")));
		for (Iterator it = map.keySet().iterator(); it.hasNext();) {
			String country = (String) it.next();
			List<String> list = map.get(country);
			for (int i = 0; i < list.size(); i++) {
				out.write(country + "	" + list.get(i));
				out.newLine();
			}
		}
		out.close();// д���ļ�һ��Ҫ�ǵùر�������
	}

	/*
	 * 
	 * �ҳ�ÿ�����Ӷ������ĸ�����
	 * */
	public void matchSentence(String filePath, Map<String, List<String>> map)
			throws Exception {
		File file = new File(filePath);
		BufferedReader in = new BufferedReader(new FileReader(file));
		BufferedWriter out = new BufferedWriter(new FileWriter("D:\\���籭\\���\\"
				+ file.getName()));
		int count = 0;
		String temp;
		int i = 1;
		String fileName = "";
		while ((temp = in.readLine()) != null) {
			if (i % 3 == 1) {
				fileName = temp;
			} else {
				String sentence[] = temp.split("��");// ��ÿ�зֳ����ɾ�
				for (int j = 0; j < sentence.length; j++) {
					// addFlag��־����ֹһ�������ظ����
					Map<String, Integer> addFlag = new HashMap<String, Integer>();
					for (Iterator it = map.keySet().iterator(); it.hasNext();) {
						String key = (String) it.next();
						addFlag.put(key, 0);
					}
					String countryName = "";
					String[] word = sentence[j].split(" ");// ��ÿ�����ӽ��зִʴ浽������
					for (int k = 0; k < word.length; k++) {
						sen: for (Iterator it = map.keySet().iterator(); it
								.hasNext();) {
							String key = (String) it.next();
							List<String> list = (List<String>) map.get(key);
							for (int m = 0; m < list.size(); m++) {
								if (list.get(m).equals(word[k])
										&& addFlag.get(key) == 0) {
									countryName += key + "��";
									addFlag.put(key, 1);
									break sen;
								}
							}
						}
					}
					if (!countryName.equals("")) {
						int len = countryName.length();
						out.write(count
								+ "	"
								+ fileName
								+ "	"
								+ countryName.substring(0, len - 1)
								+ "				"
								+ sentence[j].replace(" ", "").replaceAll("		",
										"") + "��");
						out.newLine();
					}

					count++;
				}
			}
			i++;
		}

	}

	public static void main(String[] agrs) throws Exception {
		FinalSegementation test1 = new FinalSegementation();
		test1.getSegeResult();
		System.out.println("������");
	}
}
