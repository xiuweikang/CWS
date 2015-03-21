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

//计算指标
public class CalIndex {
	//词个数统计
	int count=1;
	int docNum;
	//保存映射关系：索引——>单词
   private Map<Integer,String>indexWord = new HashMap<Integer,String>();
    //保存映射关系：单词——>索引
   private Map<String,Integer>wordIndex = new HashMap<String,Integer>();
   //存放单个单词（id）--->的出现的次数
   private Map<Integer,Integer>wordCount = new HashMap<Integer,Integer>();
   //存放组合单词（id），对应的出现的次数
   private Map<Long,Integer>wordMulCount = new HashMap<Long,Integer>();
   //保存相邻词的紧密度  词ID--->相邻词的紧密度
   private Map<Long,Float>wordClose = new HashMap<Long,Float>();
   /**
   * 构造函数
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
   * 未分簇时，得到文章的数目
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
		rand.seek(pos);//将指针移到相应的位置，才能读取。
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
   * 进行各个成员域的初始化
   * 
   */ //处理未为分簇的情况
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
   * 对一篇文章进行统计，单个词及相邻词出现的次数
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
			//下个单词不是标点符号
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
 * 计算组合词的紧密度
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
	   System.out.println("结束");
   }
}
