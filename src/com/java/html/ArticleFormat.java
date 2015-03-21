package com.java.html;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ArticleFormat {
	public void format(String file,String outPutFile,int start) throws IOException
	{
		BufferedWriter out=new BufferedWriter(new FileWriter(new File(outPutFile)));
		File inPutDir=new File(file);
		File[]dir=inPutDir.listFiles();
		int arcNum=1;
		for(int i=0;i<dir.length;i++)
		{
			BufferedReader in=new BufferedReader(new FileReader(dir[i]));
			String row1,row2,row3;
			while((row1=in.readLine())!=null)
			{
				row2=in.readLine();
				row3=in.readLine();
				out.write(arcNum+"	");
				out.write(row2.substring(start));//д�����
				out.write("��");//�����þ�Ÿ���
				out.write(row3.substring(start));//д������
				out.newLine();
				arcNum++;
				out.flush();
			}
			in.close();
		}
		out.close();
	}
	public int getDocNum(File file) 
	{
		long len=0;
		String line="";
		RandomAccessFile raf=null;
		try {
			raf=new RandomAccessFile(file, "r");
			len=raf.length();
			long pos=len-2;
			while(pos>=0)
			{
				raf.seek(pos);
				if(raf.readByte()=='\n')
				{
					line=raf.readLine();;
					break;
				}
				pos--;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String a[] =line.split("\t",2);
		return Integer.parseInt(a[0]);
	}
	public static void main(String[] args) throws IOException
	{
		ArticleFormat af=new ArticleFormat();
		File file=new File("D:\\���籭\\article.txt");
		af.format( "D:\\���籭\\�������ķִ�","D:\\���籭\\article.txt", 4);
		System.out.println(af.getDocNum(file));
	}

}
