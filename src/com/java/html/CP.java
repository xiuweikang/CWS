package com.java.html;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CP{
	public void loop(String directory,List<String> list)
	{
		File f=new File(directory);
		if(f.isDirectory())
		{
			String dir[]=f.list();//�˷����õ������ļ��������Ǿ���·�������Ի�þ���·��ʱ
			             //�õ��Ĳ���������·�����ǵ�ǰ·����eclipse����·����+getpath����·����getPathΪ�ļ�����
			for(int i=0;i<dir.length;i++)
				loop(dir[i],list);
		}
		else
			list.add(f.getPath());
	}
     public String getHtmlContent(String Url)
     {
    	 StringBuffer sb=new StringBuffer();
    	 URL url;
    	 String temp;
    	 try{
    		 url=new URL(Url);
    		 BufferedReader br=new BufferedReader(new InputStreamReader(url.openStream()));
    		 while((temp=br.readLine())!=null)//ÿһ�е��ַ��浽stringbuffer����Ϊ���ݶ�ʹ�����ռ�ڴ���
    		 {
    			 sb.append(temp);
    		 }
    		 br.close();
    	 }catch(Exception e)
    	 {
    		 e.printStackTrace();
    	 }
    	 return sb.toString();
     }
     public String crawledContent(String htmlUrlContent,String regex)
     {
    	 String crawledContent="";
    	 List<String> list=new ArrayList<String>();
    	 Pattern pa=Pattern.compile(regex,Pattern.CANON_EQ);
    	 Matcher ma=pa.matcher(htmlUrlContent);
    	 while(ma.find())
    	 {
    		 list.add(ma.group());//�õ�ƥ������һ��һ���Ϊ�����ֵڼ��������������������ʽ
    	 }
    	 for(String find:list)
    	 {
    		 crawledContent=crawledContent+find;
    	 }
    	 return crawledContent;
     }
     //�õ�������������
     public String getWE( String Url){
    	 String content;
    	 String title;
    	 String body;
    	content=getHtmlContent(Url);
         title=crawledContent(content, "<title>.*</title>");
         title=title.replaceAll("<.*?>","" );
         content=content.replaceAll("<div class=\"gg200x300\">.*?</div>", " ");
         body=crawledContent(content, "<div id=\"endText\">.*?</div>");
         body=crawledContent(body, "<p>.*?</p>");
         body=body.replaceAll("<.*?>","");
         return "���⣺\n"+title+"\n"+"����:"+body;
     }
    //�õ��Ѻ���������
     public String getSOHu(String htmlUrl){
    	 String content;
    	 String title;
    	 String body;
    	content=getHtmlContent(htmlUrl);
         title=crawledContent(content, "<title>.*</title>");
         title=title.replaceAll("<.*?>","" );
         body=crawledContent(content, "<div itemprop=\"articleBody\">.*</div>");
         body=crawledContent(body, "<p>[^�����ͷ�]*?</p>");
         body=body.replaceAll("<span.*>.*</span>","");
         body=body.replaceAll("<a.*?>.*?</a>","");
         body=body.replaceAll("<.*?>","");
    	 return "���⣺\n"+title+"\n"+"����:\n"+body;
     }
     //�õ�������������
     public String getSina(String htmlUrl) 
     {
    	 String content;
    	 String title="";
    	 String body="";
   	content=getHtmlContent(htmlUrl);
         title=crawledContent(content, "<title>.*</title>");
         title=title.replaceAll("<.*?>","" );
         body=crawledContent(content,"<div class=\"BSHARE_POP blkContainerSblkCon\" id=\"artibody\">.*</div>");
        body=crawledContent(body, "<p>.*?</p>");
        body=body.replaceAll("���˼��.*","");
         body=body.replaceAll("<.*?>","");
      return "���⣺\n"+title+"\n"+"����:"+body;
     }
     //�õ���Ѷ��������
     public String getQQ(String htmlUrl){
     	 String content;
    	 String title;
    	 String body;
   	     content=getHtmlContent(htmlUrl);
         title=crawledContent(content, "<title>.*</title>");
         title=title.replaceAll("<.*?>","" );
         body=crawledContent(content,"<div id=\"Cnt-Main-Article-QQ\" bossZone=\"content\">.*</div>");
         body=crawledContent(body, "<P style=\"TEXT-INDENT: 2em\">[^��].*?</P>");
         body=body.replaceAll("<.*?>","").replaceAll(".Ben..*","");
         return "���⣺\n"+title+"\n"+"����:\n"+body; 
     }
     public static void main(String []agr0) throws Exception
     {
    	 CP cp=new CP();
    	 String content;
    	 String title;
    	 String body;
    	content=cp.getHtmlContent("file:///D:/%E8%A7%A3%E6%9E%90/sohu/n401397258.shtml.htm");
         title=cp.crawledContent(content, "<title>.*</title>");
         title=title.replaceAll("<.*?>","" );
         body = content.replaceAll("<div class=\"text-pic\">.*?</div>", "");
 		// ��ʹ��|��ǰ���������ʽΪ������ϵ��ƥ�䵽��Χ�����
         body = cp.crawledContent(body,"<div class=\"text clear\" id=\"contentText\">.*</div>|<div class=\"textcont\" id=\"textcont\">.*?</div>");
         body = cp.crawledContent(body,"<div itemprop=\"articleBody\">.*</div>|.*");
        // System.out.println(body);
 		body = body.replaceAll(
 				"<div class=\"share clear\" id=\"share\">.*</div>", "");
 		body = cp.crawledContent(body, "(<p>[^�����ͷ�]*?</p>|.*)");
 		// System.out.println(body);
 		body = body.replaceAll("<span.*?>.*?</span>", "");
 		body = body
 				.replaceAll(
 						"<SCRIPT>.*</SCRIPT>|<script.*?>.*?</script>|<Script.*?>.*?</Script>",
 						"");
 	//	System.out.println(body);
 		body = body.replaceAll("<a.*?>.*?</a>", "");
 		body = body.replaceAll("<.*?>", "");
 		System.out.println(body);
 		body = body.replaceAll("#container.*?-->", "");
 		body = body.replaceAll("&nbsp.*?-->", "");
 		body = body.replaceAll("&nbsp", "");
 		body = body.replaceAll("\\s", "");
      System.out.println(body);
   	     }
     }

