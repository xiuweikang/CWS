package com.java.html;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class Probability {
	private final double E=2.718281828459 ;
	/*
	 * 
	 * ��ô洢����key�б�ṹ��ĳ����������������ҵĸ���value��Map
	 * */	
public void getResultMap(Map<String,Double> result,Map map)
{
	for(Iterator<String> it=map.keySet().iterator();it.hasNext();)
	{
		String key=it.next();
		result.put(key, 0.0);
//		System.out.print(key+" ");
	}
//	System.out.println();

}
/*
 * 
 * ������ÿ���ǹ��ҵ��ʣ���ÿ���ھ����г��ֹ��ҵĸ���
 * */
public void createWordResult(Map<String,Map<String,Double>>result,Map<String, List<String>> countryDic, Map<String,List<Integer>>commenWord)
{
	for(Iterator<String>it= commenWord.keySet().iterator();it.hasNext();)
	{
		String word=it.next();
		Map<String,Double>temp=new HashMap<String, Double>();
		for(Iterator<String> is=countryDic.keySet().iterator();is.hasNext();)
		{
			String country=is.next();
			temp.put(country, 0.0);
		}
		result.put(word, temp);
	}
}
/*
 * 
 * ������ÿ���ǹ��ҵ��ʣ���ÿ���ھ����г��ֹ��ҵĸ���
 * */
public void createRepeatWordResult(Map<String,Map<String,Double>>result,Map<String, List<String>> countryDic, Map<String,List<Integer>>commenWord)
{
	for(Iterator<String>it= commenWord.keySet().iterator();it.hasNext();)
	{
		String word=it.next();
		List<Integer>list=commenWord.get(word);
		for(int i=0;i<list.size();i++)
		{
			int  index=list.get(i);
			Map<String,Double>temp=new HashMap<String, Double>();
	    	for(Iterator<String> is=countryDic.keySet().iterator();is.hasNext();)
		    {
			   String country=is.next();
			   temp.put(country, 0.0);
		     }
		     result.put(index+"	"+word, temp);
		 }
	}
}
/*
 * 
 * �������ظ����ֵĽ��
 * */
public Map<String,Map<String,Double>> dealResult(Map<String,Map<String,Double>>result)
{
	Map<String,Map<String,Double>>finalResult=new TreeMap<String, Map<String,Double>>(new Comparator<String>() {

		@Override
		public int compare(String o1, String o2) {
			String[]temp1=o1.split("	",2);
			int a1=Integer.valueOf(temp1[0]);
			String[]temp2=o2.split("	",2);
			int a2=Integer.valueOf(temp2[0]);
			return a1>a2?1:(a1==a2?0:-1);
		}
	});
	finalResult.putAll(result);
    return finalResult;
}
/*
 * 
 * ��ÿ�����ӽ��д�������ֻ�������ֵ�����
 * */
public List<String> dealSen(String sentense)
{
	List<String>list=new ArrayList<String>();
	String []words=sentense.split(" ");
	for(int i=0;i<words.length;i++)
	{
		if(!words[i].equals(" "))
		{
			list.add(words[i]);
		}
	}
	return list;
}
       /*
        * 
        * 
        *����һ�仰�����ᵽ�������ҵĸ��� 
        * */
public void getSingleProbablity(Map<String,Double>result,Map<String,List<String[]>> countryWord, Map<String,List<Integer>>commenWord)
{
	double totalProbality=0;
	for(Iterator<String>it=countryWord.keySet().iterator();it.hasNext();){
		    String key=it.next();
			List<String[]>list1=countryWord.get(key);
			double countryProbality=0;
			for(int i=0;i<list1.size();i++)
			{
				String []temp=list1.get(i);
				int x=Integer.valueOf(temp[1]);
				double probality=0;
				
				for(Iterator<String>is=commenWord.keySet().iterator();is.hasNext();)
				{
					String key1=is.next();
					List<Integer>list2=commenWord.get(key1);
					for(int j=0;j<list2.size();j++)
					{
						int y=list2.get(j);
						double temp1=-(x-y)*(x-y)/200.0;
						double temp2=Math.pow(E, temp1);
						probality+=temp2;
					}
				}
				totalProbality+=probality;
				countryProbality+=probality;
				temp[2]=Double.toString(probality);
			}
			result.put(key, countryProbality);
	 }//�����forѭ��
	for(Iterator<String>it=countryWord.keySet().iterator();it.hasNext();){
		String key=it.next();
		double temp=result.get(key)/totalProbality;
		double temp1=Math.round(temp*10000)/10000.0;
		result.put(key, temp1);
	}
}

/*
 * 
 * 
 * ����ÿ������(�ظ��ĵ���û�ϲ�)��Ӧ���ҵĸ���
 * *///result ��keyΪ����Ϊ���Ʊ��+������ݿ�˼�룬�ڶ��������ŵ����±ꡣ
public void getRepeatWordProbablity(Map<String,Map<String,Double>>result,Map<String,List<String[]>> countryWord, Map<String,List<Integer>>commenWord)
{
	Map<String,Double>wordCounPro=new HashMap<String,Double>();//���һ�����ʶ�Ӧÿ�����ҵĸ���
	for(Iterator<String>it=commenWord.keySet().iterator();it.hasNext();)
	{
		
		String word=it.next();
		List<Integer>commList=commenWord.get(word);
		for(int j=0;j<commList.size();j++)
		{
			int y=commList.get(j);
			double totalPro=0;
			for(Iterator<String>ic=countryWord.keySet().iterator();ic.hasNext();)
		    {
			    String country=ic.next();
			    double everyCounPro=0;
			    List<String[]> counWorList=countryWord.get(country);
			    for(int i=0;i<counWorList.size();i++)
			    {
				    String[]wordArr=counWorList.get(i);
				    int x=Integer.valueOf(wordArr[1]);
				        double temp1=-(x-y)*(x-y)/200.0;
				        double temp2=Math.pow(E, temp1);
				        totalPro+=temp2;
				        everyCounPro+=temp2;
			    }
			    wordCounPro.put(country, everyCounPro);
		    }
			 Map<String,Double>a= result.get(y+"	"+word);
			   for(Iterator<String>iw=wordCounPro.keySet().iterator();iw.hasNext();)
			   {
				   String key=iw.next();
				   double temp3=wordCounPro.get(key)/totalPro; 
				   double temp4=Math.round(temp3*10000)/10000.0;
				   a.put(key, temp4);
				//   System.out.println(temp4+"");
			   } 
			   wordCounPro.clear();			  
		}
	}
}
/*
 * 
 * 
 * ����ÿ������(�ظ��ĵ��ʺϲ���)��Ӧ���ҵĸ���
 * */
public void getWordProbablity(Map<String,Map<String,Double>>result,Map<String,List<String[]>> countryWord, Map<String,List<Integer>>commenWord)
{
	Map<String,Double>wordCounPro=new HashMap<String,Double>();//���һ�����ʶ�Ӧÿ�����ҵĸ���
	for(Iterator<String>it=commenWord.keySet().iterator();it.hasNext();)
	{
		double totalPro=0;
		String word=it.next();
		List<Integer>commList=commenWord.get(word);

		   for(Iterator<String>ic=countryWord.keySet().iterator();ic.hasNext();)
		   {
			  String country=ic.next();
			  double everyCounPro=0;
			  List<String[]> counWorList=countryWord.get(country);
			  for(int i=0;i<counWorList.size();i++)
			  {
				    String[]wordArr=counWorList.get(i);
				    int x=Integer.valueOf(wordArr[1]);
					for(int j=0;j<commList.size();j++)
					{
						int y=commList.get(j);
				        double temp1=-(x-y)*(x-y)/200.0;
				        double temp2=Math.pow(E, temp1);
				        totalPro+=temp2;
				       // System.out.println(temp2+"");
				        everyCounPro+=temp2;
					}
			  }
			//  System.out.println(everyCounPro+"");
			  wordCounPro.put(country, everyCounPro);
		   }
		       Map<String,Double>a= result.get(word);
			   for(Iterator<String>iw=wordCounPro.keySet().iterator();iw.hasNext();)
			   {
				   String key=iw.next();
				   double temp3=wordCounPro.get(key)/totalPro; 
				   double temp4=Math.round(temp3*10000)/10000.0;
				   a.put(key, temp4);
				//   System.out.println(temp4+"");
			   } 
			   wordCounPro.clear();
	}
}
/*
 * 
 *  �õ����Ҵʺͷǹ��Ҵ�
 * */
public void getCouAndCommWord(Map<String, List<String>> countryDic, Map<String,List<String[]>> countryWord,Map<String,List<Integer>>commenWord,String str) throws Exception{	  
	 //�Ծ��ӽ��д���ȡ������
	 List<String>words=dealSen(str);
	 //���ԭ���Ӽ�ȥ���ո�
	 String sentence=str.replaceAll(" ", "");

	 for(int i=0;i<words.size();i++)
	 {
		 int flag=0;//��־�Ƿ��ǹ��ҵ���
		 int  index=sentence.indexOf(words.get(i));
		  //�ȿ��Ƿ�Ϊ���Ҵ�
		 for(Iterator<String> it=countryDic.keySet().iterator();it.hasNext()&&!(words.get(i)).matches("��+");)
		 {
			 
			 String key=it.next();
			 if(countryDic.get(key).contains(words.get(i)))
			 {
				 flag=1;
				 String[]temp=new String[3];
				 temp[0]=words.get(i);
				 temp[1]=Integer.toString(index);
				 if(countryWord.get(key)==null)
				 {
					 List<String[]>list=new ArrayList<String[]>();
					 list.add(temp);
				     countryWord.put(key,list);	 
				 }
				 else{
					 //�жϴ˴��漰�Ĺ����ڴ˴�ǰ�Ƿ���ֹ���������Ҵʣ������ֹ�����index�Ŀ�ʼλ�øı�
					 List<String[]>tempList=countryWord.get(key);
					 tag1:
				    for(int k=tempList.size()-1;k>=0;k--)
				    {
				    	String []temp2=tempList.get(k);
				    	if(temp2[0].equals(words.get(i)))
				    	{
				    		int start=Integer.valueOf(temp2[1])+1;
				    		index=sentence.indexOf(words.get(i),start);
				    		temp[1]=Integer.toString(index);
				    		break tag1;
				    	}
				    }
					countryWord.get(key).add(temp);	 
				
				 }
				break; 
			 }//ifbiazhi 	 
		 }
		 //�����ǹ��Ҵʵ�ʱ���������ͨ��
		 if(!(words.get(i)).matches("��+"))
		 {
			 if(flag==0)
		    {
			 List<Integer>temp=new ArrayList<Integer>();
			 if(commenWord.get(words.get(i))==null)
			 {
				 temp.add(index);
				 commenWord.put(words.get(i), temp);
			 }
			 else
			 {
				 
				 List<Integer> list=commenWord.get(words.get(i));
				 int start=list.get(list.size()-1)+1;
				 index=sentence.indexOf(words.get(i),start);
				 list.add(index);
			 }
		 }
		}
	 }
    
}
/*
 * 
 * ���õ�ÿ���������ڸ������ҵĸ���
 * */
public void  getArticlePro(Map<String,Double>result,Map<String, List<String>> countryDic, Map<String,List<String[]>> countryWord,Map<String,List<Integer>>commenWord,String str) throws Exception
{
	getCouAndCommWord(countryDic,countryWord,commenWord,str);
	 //����˾��Ӷ���ÿ�����ҵĸ���
      getSingleProbablity(result, countryWord, commenWord);
    //  System.out.println(result);
}
/*
 * 
 * �õ�ÿ�����ӵ�ÿ�����ҵĸ��� ��д��txt�ļ�
 * */
public void getAllArticleProbability(int n) throws Exception
{
	BufferedReader in=new BufferedReader(new FileReader(new File("D:/���籭/1.txt")));
	BufferedWriter out=new BufferedWriter(new FileWriter(new File("D:/���籭/11.txt")));
	String row;
	
	 //key��Ź��� valueΪ��������������ڵĵ����б��б�Ϊ����Ϊ3������ 1 ���ҵ��� 2 �����ھ��ӵ��±� 3 �ǹ��ҵ��� ����������ʵĸ��� 
	 Map<String,List<String[]>> countryWord=new HashMap<String, List<String[]>>();
	 //��ŷǹ��ҵ���,���ھ����е��±�
	 Map<String,List<Integer>>commenWord=new HashMap<String, List<Integer>>();
	FinalSegementation test=new FinalSegementation();
	Map<String, List<String>> countryDic= test.getAllInformation("D:\\���籭\\���Ҵʵ�");
	 //��ñ������ս����Map
	Map<String,Double> result=new HashMap<String, Double>();
	getResultMap(result,countryDic);
	int i=1;
	String article="";
	while((row=in.readLine())!=null)
	{
		String[]colums=row.split("	",2);
		String fileIndex=colums[0];
		if(Integer.valueOf(fileIndex)==i)
		{
			article+=colums[1]+" ";
            for(int j=0;j<n;j++)
            {
            	article+="��";
            }
            article+=" ";
            System.out.println(article);
		}
		else
		{
			getArticlePro(result,countryDic, countryWord,commenWord,article);
		    out.write(Integer.toString(i));
		    for(Iterator<String>it=result.keySet().iterator();it.hasNext();)
		    {
			    out.write("\t");
			    String key=it.next();
			    if(result.get(key)==0.0)
				  out.write("      ");
			    else
				  out.write(Double.toString(result.get(key)));
        	}
			//��ս��  ��һ��ѭ������ʹ��
			out.newLine();
			countryWord.clear();
			commenWord.clear();
			//���ý��
			getResultMap(result,countryDic);
			i++;
			article=colums[1]+" ";
            for(int j=0;j<n;j++)
            {
            	article+="��";
            }
            article+=" ";
		}
	}
	out.close();
	in.close();
}
/*
 * 
 * �õ�ÿ��������ÿ�����ҵĸ��ʲ���д���ļ�
 * */
public void getAllWordProbability() throws Exception
{
	BufferedReader in=new BufferedReader(new FileReader(new File("D:/���籭/worldcup3_sen_10_nopunc.txt")));
	BufferedWriter out=new BufferedWriter(new FileWriter(new File("D:/���籭/WordProbability.txt")));
	String row;
	 //key��Ź��� valueΪ��������������ڵĵ����б��б�Ϊ����Ϊ3������ 1 ���ҵ��� 2 �����ھ��ӵ��±� 3 �ǹ��ҵ��� ����������ʵĸ��� 
	 Map<String,List<String[]>> countryWord=new HashMap<String, List<String[]>>();
	 //��ŷǹ��ҵ���,���ھ����е��±�
	 Map<String,List<Integer>>commenWord=new HashMap<String, List<Integer>>();
	FinalSegementation test=new FinalSegementation();
	Map<String, List<String>> countryDic= test.getAllInformation("D:\\���籭\\���Ҵʵ�");
	 //��ñ������ս����Map
	Map<String,Map<String,Double>>result=new HashMap<String,Map<String,Double>>();
	while((row=in.readLine())!=null)
	{
		String[]colums=row.split("	",2);
		String fileIndex=colums[0];
		getCouAndCommWord(countryDic, countryWord,commenWord,colums[1]);
		createRepeatWordResult(result,countryDic, commenWord);
		getRepeatWordProbablity(result,countryWord,commenWord);
		result=dealResult(result);
		//System.out.println(result);
		for(Iterator<String>it=result.keySet().iterator();it.hasNext();)
		{
			String key=it.next();
			String key1[]=key.split("	",2);
			out.write(fileIndex+"\t"+key1[1]);
			Map<String,Double>map=result.get(key);
			for(Iterator<String>im=map.keySet().iterator();im.hasNext();)
			{
				out.write("\t");
				String coun=im.next();
			   if(map.get(coun)==0)
				 out.write("      ");
			   else
				 out.write(Double.toString(map.get(coun)));
			 }
			out.newLine();
		}
		//��ս��  ��һ��ѭ������ʹ��
		countryWord.clear();
		commenWord.clear();
		//���ý��
		result.clear();
		
	}
	out.close();
	in.close();
}
  public static void main(String []args) throws Exception 
  {
	 Probability pr=new Probability();
    // pr.getAllWordProbability();
	 pr.getAllArticleProbability(5);
     System.out.println("������������yeah");
  }
}
