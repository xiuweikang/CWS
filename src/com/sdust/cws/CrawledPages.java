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
	 * �õ�buffer����
	 * */
	public BufferedWriter getBufferedWriter(String file) throws IOException {
		File f = new File(file);
		return new BufferedWriter(new FileWriter(f));
	}

	/*
	 * 
	 * ��ȡ�ļ�Ŀ¼������html�ļ�
	 * */
	public void loop(String directory, List<String> list) {// Ҫ��þ���·������ÿ�㶼�������·������ע��List���ǻ�þ���·��
		File f = new File(directory);
		if (f.isDirectory()) {
			String dir[] = f.list();// �˷����õ������ļ��������Ǿ���·�������Ի�þ���·��ʱ
			// �õ��Ĳ���������·�����ǵ�ǰ·����eclipse����·����+getpath����·����getPathΪ�ļ�����
			for (int i = 0; i < dir.length; i++)
				loop(f.getAbsolutePath() + "/" + dir[i], list);
		} else
			list.add(f.getAbsolutePath());
	}

	/*
	 * 
	 * 
	 * �õ�url�ļ�����
	 * */
	public String getHtmlContent(String Url) {
		StringBuffer sb = new StringBuffer();
		URL url;
		String temp;
		try {
			url = new URL(Url);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					url.openStream()));
			while ((temp = br.readLine()) != null)// ÿһ�е��ַ��浽stringbuffer����Ϊ���ݶ�ʹ�����ռ�ڴ���
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
	 * ץȡ����
	 * */
	public String crawledContent(String htmlUrlContent, String regex) {
		String crawledContent = "";
		List<String> list = new ArrayList<String>();
		Pattern pa = Pattern.compile(regex);
		Matcher ma = pa.matcher(htmlUrlContent);
		while (ma.find()) {
			list.add(ma.group());// �õ�ƥ������һ��һ���Ϊ�����ֵڼ��������������������ʽ
		}
		for (String find : list) {
			crawledContent = crawledContent + find;
		}
		return crawledContent;
	}

	/*
	 * 
	 * �õ�����һ����ҳ��������
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
	 * �õ��Ѻ�һ����ҳ��������
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
		body = crawledContent(body, "(<p>[^�����ͷ�]*?</p>|.*)");
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
	 * �õ�����һ����ҳ��������
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
		body = body.replaceAll("���˼��.*", "");
		body = body.replaceAll("<.*?>", "");
		body = body.replaceAll("&nbsp.*-->|&nbsp", "");
		result[0] = title;
		result[1] = body;
		return result;
	}

	/*
	 * 
	 * �õ���Ѷһ����ҳ��������
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
	 * �õ�����һ����ҳ��������
	 * */
	public void getAllWE(String filepath) throws Exception {
		String[] result = new String[2];
		List<String> list = new ArrayList();
		BufferedWriter in = this.getBufferedWriter("d:\\����.txt");
		loop(filepath, list);// ��ʵʵ�ʵ�·��Ϊһ��\����\Ϊת���ַ�����Ҫ������
		for (int i = 0; i < list.size(); i++) {
			File f = new File(list.get(i));
			String path = f.toURI().toURL().toString();
			result = getWE(path);
			if (f.getName().matches(
					"(\\d+\\.html)|(.*-.*\\.html)|([a-z]+\\.html)")) {
			} else if (f.getName().matches(".*.html")) {
				in.write("�ļ�����" + f.getName());
				in.newLine();
				in.write("���⣺" + result[0]);
				in.newLine();
				in.write("���ģ�" + result[1]);
				in.newLine();
				in.flush();
			}

		}
		in.close();
	}

	/*
	 * 
	 * �õ������Ѻ�������������Ϣ
	 * */
	public void getAllSoHu(String filepath) throws Exception {
		List<String> list = new ArrayList();
		String[] result = new String[2];
		BufferedWriter in = this.getBufferedWriter("D:\\���籭\\��������\\�Ѻ�.txt");
		loop(filepath, list);
		for (int i = 0; i < list.size(); i++) {
			File f = new File(list.get(i));

			if (f.getName().matches("index.*")) {
				System.out.println("�ⲻ������" + list.get(i));
			} else {
				result = getSoHu(f.toURI().toURL().toString());
				in.write("�ļ�����" + f.getName());
				in.newLine();
				in.write("���⣺" + result[0]);
				in.newLine();
				in.write("���ģ�" + result[1]);
				in.newLine();
				in.flush();

			}
		}
		in.close();
	}

	/*
	 * 
	 * �õ������Ѻ�������������Ϣ
	 * */
	public void getAllSina(String filepath) throws Exception {
		List<String> list = new ArrayList();
		String[] result = new String[2];
		BufferedWriter in = this.getBufferedWriter("d:\\����.txt");
		loop(filepath, list);
		for (int i = 0; i < list.size(); i++) {
			File f = new File(list.get(i));

			if (f.getName().matches(
					"index.*|\\d.html|[a-z]*.html|\\d.shtml.htm")) {
				System.out.println("�ⲻ������" + list.get(i));
			} else if (f.getName().matches(".*.shtml.htm")) {
				result = getSina(f.toURI().toURL().toString());
				in.write("�ļ�����" + f.getName());
				in.newLine();
				in.write("���⣺" + result[0]);
				in.newLine();
				in.write("���ģ�" + result[1]);
				in.newLine();
				in.flush();
			}

		}
		in.close();
	}

	/*
	 * 
	 * �õ���Ѷ�Ѻ�������������Ϣ
	 * */
	public void getAllQQ(String filepath) throws Exception {
		List<String> list = new ArrayList();
		String[] result = new String[2];
		BufferedWriter in = this.getBufferedWriter("d:\\��Ѷ.txt");
		loop("D:\\����\\QQ", list);
		for (int i = 0; i < list.size(); i++) {
			File f = new File(list.get(i));
			String path = f.toURI().toURL().toString();
			result = getQQ(path);
			String con = result[0] + result[1];
			if (!con.equals("") && f.getName().matches("\\d{6,}.*.htm")) {
				in.write("�ļ�����" + f.getName());
				in.newLine();
				in.write("���⣺" + result[0]);
				in.newLine();
				in.write("���ģ�" + result[1]);
				in.newLine();
				in.flush();
			}
		}
		in.close();
	}

	public static void main(String[] agr0) throws Exception {
		CrawledPages cp = new CrawledPages();

		// cp.getAllWE("D:\\���籭\\������Դ\\163");
		cp.getAllSoHu("D:\\���籭\\������Դ\\�Ѻ�");
		System.out.println("������������");
		// cp.getAllSina("D:\\����\\sina");
		// cp.getAllQQ("D:\\����\\QQ");
	}

}