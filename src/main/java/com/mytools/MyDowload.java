package com.mytools;

import javax.swing.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.mytools.util.DownloadUtils;
import com.mytools.util.ImgMatchUtil;
import com.mytools.util.MyHttpsUtils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MyDowload {
	public static Gson gson = new Gson();

	public static volatile boolean Runing = false;

	public static void main(String[] args) throws Exception {

		final JFrame jf = new JFrame("工具窗口");
		jf.setSize(700, 500);
		jf.setLocationRelativeTo(null);
		jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		jf.setLayout(new BorderLayout());

		// 创建一个 5 行 10 列的文本区域
		final JTextArea textArea = new JTextArea(20, 50);
		// 设置自动换行
		textArea.setLineWrap(true);
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(28, 209, 752, 265);
		scrollPane_1.setViewportView(textArea);
		jf.add(scrollPane_1, BorderLayout.CENTER);

		final JTextArea textArea2 = new JTextArea(10, 20);
		// 设置自动换行
		textArea2.setLineWrap(true);
		textArea2.setEditable(false);
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(100, 100, 100, 100);
		scrollPane_2.setViewportView(textArea2);
		jf.add(scrollPane_2, BorderLayout.EAST);

		// 创建一个提交按钮，点击按钮获取输入文本
		JPanel panel = new JPanel();
		JButton btn = new JButton("开始下载");

		panel.add(btn);
		jf.add(panel, BorderLayout.NORTH);
		//////
		JPanel panelboot = new JPanel();

		final JTextField textField = new JTextField("D:\\File", 30);
		textField.setFont(new Font(null, Font.PLAIN, 15));
		panelboot.add(textField);

		JButton btn2 = new JButton("选择图片存储路径");
		btn2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showFileSaveDialog(jf, textField);
			}
		});
		panelboot.add(btn2);
		jf.add(panelboot, BorderLayout.SOUTH);

		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (textArea.getText() == null || "".equals(textArea.getText().trim())) {
					JOptionPane.showMessageDialog(jf, "请先输入要下载页面的源码", "错误", JOptionPane.WARNING_MESSAGE);
					return;
				}
				if (!Runing) {
					synchronized (gson) {
						if (Runing) {
							return;
						} else {
							Runing = true;
						}
					}
					new Thread(() -> {
						download(textArea.getText(), textField.getText(), textArea2, jf);
					}).start();
				} else {
					JOptionPane.showMessageDialog(jf, "上个任务还在执行中！请稍后", "错误", JOptionPane.WARNING_MESSAGE);
					return;
				}

			}
		});

		jf.setVisible(true);
	}

	private static String showFileSaveDialog(Component parent, JTextField msgTextArea) {
		// 创建一个默认的文件选取器
		JFileChooser fileChooser = new JFileChooser();

		// 设置打开文件选择框后默认输入的文件名
//        fileChooser.setSelectedFile(new File("测试文件.zip"));

		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		// 打开文件选择框（线程将被阻塞, 直到选择框被关闭）
		int result = fileChooser.showSaveDialog(parent);

		if (result == JFileChooser.APPROVE_OPTION) {
			// 如果点击了"保存", 则获取选择的保存路径
			File file = fileChooser.getSelectedFile();
			msgTextArea.setText(file.getAbsolutePath());
			return file.getAbsolutePath();
		}
		return "";
	}

	private static synchronized void download(String html, String path, JTextArea textArea, JFrame jf) {
		try {
			StringBuilder sb = new StringBuilder();
			int count = 0;
			if (path == null || "".equals(path)) {
				path = "D:\\File";
			}
			path = path + "\\";
			Document doc = Jsoup.parse(html);
			String title = doc.title();

			Element J_ItemList = doc.getElementById("J_ItemList");
			Elements items = J_ItemList.getElementsByClass("productTitle");

			sb.append("店名").append(",").append("类型").append(",").append("价格").append(",").append("销量(该店所有类型总成交数)")
				.append(",").append("评价数")		
				.append(",").append("图片")
				.append(",").append("图片在线预览地址")
				.append(",").append("店铺地址")
				.append("\n");

			
			for (Element i : items) {
				String chengjiao = "";
				String pingjia = "";
				try {
					try {
						chengjiao = ((Element) i.parentNode()).getElementsByClass("productStatus").select("em").text();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						pingjia = ((Element) i.parentNode()).getElementsByClass("productStatus").select("a").text();
					} catch (Exception e) {
						e.printStackTrace();
					}
					String url = i.selectFirst("a").attr("href");
					url = "https:" + url.substring(0, url.indexOf("&user_id"));
					String data = MyHttpsUtils.get(url, "GBK");
					Document datadoc = Jsoup.parse(data);
					Elements titles = datadoc.getElementsByAttributeValue("data-spm", "1000983");
					String titleString = "";
					if (titles != null) {
						titleString = titles.get(0).ownText();
					}

					/// 价格
					Map skuMap = new HashMap();
					try {
						List<String> list2 = ImgMatchUtil.getMatchList(data, "\\{\"valItemInfo.*", true);
						Map valItemInfomap = gson.fromJson(list2.get(0), Map.class);
						skuMap = (Map) ((Map) valItemInfomap.get("valItemInfo")).get("skuMap");
					} catch (Exception e) {
						e.printStackTrace();
					}
					//
					Elements lis = datadoc.getElementsByClass("tb-img").select("li");
					for (Element li : lis) {
						try {
							System.out.println("===========================");
							System.out.println(li);
							String type = ".jpg";
							Element a = li.selectFirst("a");
							String realurl = "";
							if (a != null) {
								String aurl = a.attr("style");
								if ("".equals(aurl)) {
									continue;
								}
								int last = aurl.indexOf(".jpg");
								if (last == -1) {
									last = aurl.indexOf(".png");
									if (last == -1) {
										continue;
									}
									type = ".png";
								}
								realurl = "http:" + aurl.substring(aurl.indexOf("//img."), last + 4);
							}
							String prdName = li.attr("title");

							String datavalue = li.attr("data-value");
							String price = "0";
							try {
								for (Object key : skuMap.keySet()) {
									String keyString = (String) key;
									if (keyString.contains(datavalue)) {
										price = (String) ((Map) skuMap.get(keyString)).get("price");
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							String realpath =  titleString.replace("/", "-").replace("\\", "-") + "&&"
									+ prdName.replace("/", "-").replace("\\", "-") + "&&" + price + "￥&&" + chengjiao
									+ "&&" +pingjia+"&&"+ System.currentTimeMillis() + type;
							realpath=replceErrorPath(realpath);
							realpath=path +realpath;
							System.out.println(realpath);
							DownloadUtils.download(realurl, realpath);

							count++;
							sb.append(titleString).append(",").append(prdName).append(",").append(price)
							.append(",").append(chengjiao)
							.append(",").append(pingjia)
							.append(",").append(realpath)
							.append(",").append(realurl)
									.append(",").append(url)
									.append("\n");
							textArea.append("程序正在执行中下载第" + count + "张图片" + "\n");
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				File fout = new File(path + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + ".csv");
				FileOutputStream fos = new FileOutputStream(fout);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
				bw.write(sb.toString());
				bw.newLine();
				bw.flush();
				bw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			JOptionPane.showMessageDialog(jf, "下载完成", "提示", JOptionPane.WARNING_MESSAGE);

		} finally {
			Runing = false;
		}
	}
	
	public static String replceErrorPath(String path) {
		if(path==null) {
			return "";
		}
		path=path.trim();
		path=path.replace("\"", "-");
		path=path.replace(":", "-");
		path=path.replace("*", "x");
		path=path.replace("?", "-");
		path=path.replace("\\", "-");
		path=path.replace("<", "(");
		path=path.replace(">", ")");
		path=path.replace(">", ")");
		path=path.replace("|", "-");
		
		return path;
	}

}
