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

//����ָ��
public class CalIndex {
	//�ʸ���ͳ��
	int count=1;
	int docNum;
	//����ӳ���ϵ����������>����
   private Map<Integer,String>indexWord = new HashMap<Integer,String>();
    //����ӳ���ϵ�����ʡ���>����
   private Map<String,Integer>wordIndex = new HashMap<String,Integer>();
   //��ŵ������ʣ�id��--->�ĳ��ֵĴ���
   private Map<Integer,Integer>wordCount = new HashMap<Integer,Integer>();
   //�����ϵ��ʣ�id������Ӧ�ĳ��ֵĴ���
   private Map<Long,Integer>wordMulCount = new HashMap<Long,Integer>();
   //�������ڴʵĽ��ܶ�  ��ID--->���ڴʵĽ��ܶ�
   private Map<Long,Float>wordClose = new HashMap<Long,Float>();
   /**
   * ���캯��
   */
public CalIndex(String file) throws IOException{
	   init_all(file);
	   this.docNum=getDocNum(file);
   }
public CalIndex(String file,int []cluster) throws IOException
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
	temp=line.split("	",2);
	docNum=Integer.parseInt(temp[0]);
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
private void init_part(String []words) {
	for(int i=0;i<words.length;i++)
	{
		if(!words[i].matches("\\pP"))
		{
			if(!wordIndex.containsKey(words[i]))
			{
				wordIndex.put(words[i], count);
				indexWord.put(count++, words[i]);
			}
			
			int index=wordIndex.get(words[i]);
			
			if(!wordCount.containsKey(index))
			   wordCount.put(index, 1);
			else
			{
				int old=wordCount.get(index);
				wordCount.put(index, ++old );
			}
			//�¸����ʲ��Ǳ�����
			if(i+1<words.length&&!words[i+1].matches("\\pP"))
			{
				if(!wordIndex.containsKey(words[i+1]))
				{
					wordIndex.put(words[i+1], count);
					indexWord.put(count++, words[i+1]);
				}
				long Lindex=wordIndex.get(words[i]),Rindex=wordIndex.get(words[i+1]);
				long newKey=Lindex*100000+Rindex;
				if(!wordMulCount.containsKey(newKey))
					wordMulCount.put(newKey, 1);
				else
				{
					int old=wordMulCount.get(newKey);
					wordMulCount.put(newKey, ++old );
				}
			}
			else
				i++;
		}
	}
}
/**
 * ������ϴʵĽ��ܶ�
 */
public void calClose()
{
	for(Iterator<Long>it=wordMulCount.keySet().iterator();it.hasNext();)
	{
		long doubleID=it.next();
		
		int leftID=(int) (doubleID/100000),rightID=(int) (doubleID%100000);
		//System.out.println(doubleID+" "+leftID+" "+wordCount.get(leftID)+" "+rightID+" "+wordCount.get(rightID));
		double temp=(docNum*wordMulCount.get(doubleID)*1.0)/(wordCount.get(leftID)*wordCount.get(rightID)); 
		float close=(float) Math.log(temp);
        wordClose.put(doubleID, close);	    
	}
}
public void printClose(String file) throws IOException
{
	BufferedWriter out=new BufferedWriter(new FileWriter(file));
	String left="",right="";
	for(Iterator<Long>it=wordClose.keySet().iterator();it.hasNext();)
	{
		long doubleID=it.next();
		int leftID=(int) (doubleID/100000),rightID=(int) (doubleID%100000);
		left=indexWord.get(leftID);
		right=indexWord.get(rightID);
		out.write(left+" "+right+"	");
		out.write(Float.toString(wordClose.get(doubleID)));
		out.newLine();
		out.flush();
	}
	out.close();
}
public static void main(String[]args) throws IOException
   {
	   CalIndex cal=new CalIndex("D:\\article.txt",new int[]{52,68});
	   cal.calClose();
	   cal.printClose("D:\\close.txt");
	   System.out.println("����");
   }
}
