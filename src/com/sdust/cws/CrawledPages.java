package com.sdust.cws;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrawledPages {
	/*
	 * 得到buffer对象
	 * */
	public BufferedWriter getBufferedWriter(String file) throws IOException {
		File f = new File(file);
		return new BufferedWriter(new FileWriter(f));
	}

	/*
	 * 
	 * 获取文件目录中所有html文件
	 * */
	public void loop(String directory, List<String> list) {// 要获得绝对路径名则每层都传入绝对路径名，注意List不是获得绝对路径
		File f = new File(directory);
		if (f.isDirectory()) {
			String dir[] = f.list();// 此方法得到的是文件名，不是绝对路径，所以获得绝对路径时
			// 得到的不是真正的路径而是当前路径（eclipse编译路径）+getpath（）路径（getPath为文件名）
			for (int i = 0; i < dir.length; i++)
				loop(f.getAbsolutePath() + "/" + dir[i], list);
		} else
			list.add(f.getAbsolutePath());
	}

	/*
	 * 
	 * 
	 * 得到url文件内容
	 * */
	public String getHtmlContent(String Url) {
		StringBuffer sb = new StringBuffer();
		URL url;
		String temp;
		try {
			url = new URL(Url);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					url.openStream()));
			while ((temp = br.readLine()) != null)// 每一行的字符存到stringbuffer中因为内容多使用这个占内存少
			{
				sb.append(temp);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/*
	 * 
	 * 抓取内容
	 * */
	public String crawledContent(String htmlUrlContent, String regex) {
		String crawledContent = "";
		List<String> list = new ArrayList<String>();
		Pattern pa = Pattern.compile(regex);
		Matcher ma = pa.matcher(htmlUrlContent);
		while (ma.find()) {
			list.add(ma.group());// 得到匹配内容一块一块的为了区分第几个内容用数组或链表形式
		}
		for (String find : list) {
			crawledContent = crawledContent + find;
		}
		return crawledContent;
	}

	/*
	 * 
	 * 得到网易一个网页新闻内容
	 * */
	public String[] getWE(String Url) {
		String content;
		String[] result = new String[2];
		String title = "";
		String body = "";
		content = getHtmlContent(Url);
		title = crawledContent(content, "<title>.*</title>");
		title = title.replaceAll("<.*?>", "");
		content = content.replaceAll("<div class=\"gg200x300\">.*?</div>", " ");
		body = crawledContent(
				content,
				"<div ((id=\"endText\" class=\"entext\")|id=\"endText\"|class=\"entext\"|(id=\"endText\" class=\"endtext\"))>.*?</div>");
		body = crawledContent(body, "(<p.*?>.*?</p>|.*)");
		body = body.replaceAll("<script.*?>.*?</script>", "");
		body = body.replaceAll("<.*?>", "");
		body = body.replaceAll("\\s", "");
		body = body.replaceAll("&nbsp.*-->|&nbsp", "");
		result[0] = title;
		result[1] = body;
		return result;
	}

	/*
	 * 
	 * 得到搜狐一个网页新闻内容
	 * */
	public String[] getSoHu(String htmlUrl) {
		String content;
		String title;
		String body;
		String[] result = new String[2];
		content = getHtmlContent(htmlUrl);
		title = crawledContent(content, "<title>.*</title>");
		title = title.replaceAll("<.*?>", "");
		body = content.replaceAll("<div class=\"text-pic\">.*?</div>", "");
		body = crawledContent(
				body,
				"<div class=\"text clear\" id=\"contentText\">.*</div>|<div class=\"textcont\" id=\"textcont\">.*?</div>");
		body = crawledContent(body, "<div itemprop=\"articleBody\">.*</div>|.*");
		body = body.replaceAll(
				"<div class=\"share clear\" id=\"share\">.*</div>", "");
		body = crawledContent(body, "(<p>[^【】客服]*?</p>|.*)");
		body = body.replaceAll("<span.*?>.*?</span>", "");
		body = body
				.replaceAll(
						"<SCRIPT>.*</SCRIPT>|<script.*?>.*?</script>|<Script.*?>.*?</Script>",
						"");
		body = body.replaceAll("<a.*?>.*?</a>", "");
		body = body.replaceAll("<.*?>", "");
		body = body.replaceAll("#container.*?-->", "");
		body = body.replaceAll("&nbsp.*?-->", "");
		body = body.replaceAll("&nbsp", "");
		body = body.replaceAll("\\s", "");
		result[0] = title;
		result[1] = body;
		return result;
	}

	/*
	 * 
	 * 得到新浪一个网页新闻内容
	 * */
	public String[] getSina(String htmlUrl) {
		String content;
		String title;
		String body;
		String[] result = new String[2];
		content = getHtmlContent(htmlUrl);
		title = crawledContent(content, "<title>.*</title>");
		title = title.replaceAll("<.*?>", "");
		content = content.replaceAll("<div align=\"center\".*?>.*?</div>", "");
		body = crawledContent(
				content,
				"<div class=\"BSHARE_POP blkContainerSblkCon\" id=\"artibody\">.*</div>| <div class=\"blk_body\" id=\"artibody\">.*?</div>");
		body = crawledContent(body, "<p>.*?</p>");
		body = body.replaceAll("新浪简介.*", "");
		body = body.replaceAll("<.*?>", "");
		body = body.replaceAll("&nbsp.*-->|&nbsp", "");
		result[0] = title;
		result[1] = body;
		return result;
	}

	/*
	 * 
	 * 得到腾讯一个网页新闻内容
	 * */
	public String[] getQQ(String htmlUrl) {
		String content;
		String title = "";
		String body = "";
		content = getHtmlContent(htmlUrl);
		String[] result = new String[2];
		if (crawledContent(content, "<body id=\"P-QQ\">.*</body>").equals("")) {
			title = crawledContent(content, "<title>.*</title>");
			title = title.replaceAll("<.*?>", "");
			body = crawledContent(content,
					"<div id=\"Cnt-Main-Article-QQ\" bossZone=\"content\">.*</div>");
			body = crawledContent(body, "<P.*?>.*?</P>");
			body = body.replaceAll("<script.*?>.*?</script>", "");
			body = body.replaceAll("<style.*?>.*?</style>", "");
			body = body.replaceAll("<a.*?>.*?</a>", "");
			body = body.replaceAll("<!--.*?>.*?<!.*?>", "");
			body = body.replaceAll("<label.*?>.*?</label>", "");
			body = body.replaceAll("<span.*?>.*?</span>", "");
			body = body.replaceAll("<.*?>", "").replaceAll(".Ben..*", "");
			body = body.replaceAll("&nbsp.*-->|&nbsp", "");
			body = body.replaceAll("\\s", "");
		}
		result[0] = title;
		result[1] = body;
		return result;
	}

	/*
	 * 
	 * 得到网易一个网页新闻内容
	 * */
	public void getAllWE(String filepath) throws Exception {
		String[] result = new String[2];
		List<String> list = new ArrayList();
		BufferedWriter in = this.getBufferedWriter("d:\\网易.txt");
		loop(filepath, list);// 其实实际的路径为一个\但是\为转移字符所以要用两个
		for (int i = 0; i < list.size(); i++) {
			File f = new File(list.get(i));
			String path = f.toURI().toURL().toString();
			result = getWE(path);
			if (f.getName().matches(
					"(\\d+\\.html)|(.*-.*\\.html)|([a-z]+\\.html)")) {
			} else if (f.getName().matches(".*.html")) {
				in.write("文件名：" + f.getName());
				in.newLine();
				in.write("标题：" + result[0]);
				in.newLine();
				in.write("正文：" + result[1]);
				in.newLine();
				in.flush();
			}

		}
		in.close();
	}

	/*
	 * 
	 * 得到网易搜狐的所有新闻信息
	 * */
	public void getAllSoHu(String filepath) throws Exception {
		List<String> list = new ArrayList();
		String[] result = new String[2];
		BufferedWriter in = this.getBufferedWriter("D:\\世界杯\\标题正文\\搜狐.txt");
		loop(filepath, list);
		for (int i = 0; i < list.size(); i++) {
			File f = new File(list.get(i));

			if (f.getName().matches("index.*")) {
				System.out.println("这不是新闻" + list.get(i));
			} else {
				result = getSoHu(f.toURI().toURL().toString());
				in.write("文件名：" + f.getName());
				in.newLine();
				in.write("标题：" + result[0]);
				in.newLine();
				in.write("正文：" + result[1]);
				in.newLine();
				in.flush();

			}
		}
		in.close();
	}

	/*
	 * 
	 * 得到新浪搜狐的所有新闻信息
	 * */
	public void getAllSina(String filepath) throws Exception {
		List<String> list = new ArrayList();
		String[] result = new String[2];
		BufferedWriter in = this.getBufferedWriter("d:\\新浪.txt");
		loop(filepath, list);
		for (int i = 0; i < list.size(); i++) {
			File f = new File(list.get(i));

			if (f.getName().matches(
					"index.*|\\d.html|[a-z]*.html|\\d.shtml.htm")) {
				System.out.println("这不是新闻" + list.get(i));
			} else if (f.getName().matches(".*.shtml.htm")) {
				result = getSina(f.toURI().toURL().toString());
				in.write("文件名：" + f.getName());
				in.newLine();
				in.write("标题：" + result[0]);
				in.newLine();
				in.write("正文：" + result[1]);
				in.newLine();
				in.flush();
			}

		}
		in.close();
	}

	/*
	 * 
	 * 得到腾讯搜狐的所有新闻信息
	 * */
	public void getAllQQ(String filepath) throws Exception {
		List<String> list = new ArrayList();
		String[] result = new String[2];
		BufferedWriter in = this.getBufferedWriter("d:\\腾讯.txt");
		loop("D:\\解析\\QQ", list);
		for (int i = 0; i < list.size(); i++) {
			File f = new File(list.get(i));
			String path = f.toURI().toURL().toString();
			result = getQQ(path);
			String con = result[0] + result[1];
			if (!con.equals("") && f.getName().matches("\\d{6,}.*.htm")) {
				in.write("文件名：" + f.getName());
				in.newLine();
				in.write("标题：" + result[0]);
				in.newLine();
				in.write("正文：" + result[1]);
				in.newLine();
				in.flush();
			}
		}
		in.close();
	}

	public static void main(String[] agr0) throws Exception {
		CrawledPages cp = new CrawledPages();

		// cp.getAllWE("D:\\世界杯\\解析资源\\163");
		cp.getAllSoHu("D:\\世界杯\\解析资源\\搜狐");
		System.out.println("终于运行完了");
		// cp.getAllSina("D:\\解析\\sina");
		// cp.getAllQQ("D:\\解析\\QQ");
	}

}