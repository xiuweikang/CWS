package com.java.html;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetHtml {
    public String getHtmlContent(String htmlUrl)
    {
    	StringBuffer sb=new StringBuffer();
    	String temp;
    	URL url;
    	try{
    		url=new URL(htmlUrl);
    		BufferedReader in=new BufferedReader(new InputStreamReader(url.openStream(),"GBk"));
    		//读取网页内容
    		while((temp=in.readLine())!=null)//此方法是一行一行的读取,结束后指针在此行末尾；
    		{
    			sb.append(temp);
    		}
    		in.close();
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
	
    	return sb.toString();
    }
    //提取网页标题
    public String  getTitle(String Content)
    {
    	String regex="<title>.*?</title>";
    	String  title="";
    	List<String> list=new ArrayList<String>();
    	Pattern pa=Pattern.compile(regex);
        Matcher ma=pa.matcher(Content);
    	while(ma.find())
    	{
    		list.add(ma.group());
    	}
    	for(String find:list)
    	{
    		title=title+find;
    	}
    /*	for(int i=0;i<list.size();i++)
    	{
    		title=title+list.get(i);
    	}*/
    	return title;
    }
    //得到网页的正文
  public String getBody(String con,String regex)
  {
	  String body="";
	  List<String>list=new ArrayList<String>();
	  Pattern pa=Pattern.compile(regex);
	  Matcher ma=pa.matcher(con);
	  while(ma.find())
	  {
		  list.add(ma.group());
	  }
	  for(String find:list)
	  {
		  body=find+body;
	  }
	  return body;  
  }
    public static void main(String []agr0){
        GetHtml getHtml=new GetHtml();
        String regex="<body.*>.*</body>";//此表达式先获取body
    	String htmlUrl="file:///C:/Users/lenovo/Desktop/004386.htm";
    	String con=getHtml.getHtmlContent(htmlUrl);
    	//System.out.println("网页内容"+con);
    	String title=getHtml.getTitle(con);
       // System.out.println(title);
        String body=getHtml.getBody(con,regex);
        String regex1="<div id=\"Cnt-Main-Article-QQ\" bossZone=\"content\">.*</div>";   //获取内容所在的div           
        String body1=getHtml.getBody(body, regex1);
        String regex2="<P style=\"TEXT-INDENT: 2em\">[^（].*?</P>";
        String body2=getHtml.getBody(body1, regex2);
        System.out.println(body2);
        //替换得到标题
        title= title.replaceAll("<.*?>", ""); 
        System.out.println("标题:\n"+title);
        //得到正文
        body= body2.replaceAll("<.*?>", "").replaceAll(".Ben..*","");
        System.out.println("正文:\n"+body);
    }
}
