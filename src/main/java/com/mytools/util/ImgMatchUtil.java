package com.mytools.util;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImgMatchUtil {
	/**
	 * 获得匹配正则表达式的内容
	 * 
	 * @param str               字符�?
	 * @param reg               正则表达�?
	 * @param isCaseInsensitive 是否忽略大小写，true忽略大小写，false大小写敏�?
	 * @return 匹配正则表达式的字符串，组成的List
	 */
	public static List<String> getMatchList(final String str, final String reg, final boolean isCaseInsensitive) {
		ArrayList<String> result = new ArrayList<String>();
		Pattern pattern = null;
		if (isCaseInsensitive) {
			// 编译正则表达�?,忽略大小�?
			pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
		} else {
			// 编译正则表达�?,大小写敏�?
			pattern = Pattern.compile(reg);
		}
		Matcher matcher = pattern.matcher(str);// 指定要匹配的字符�?
		while (matcher.find()) { // 此处find（）每次被调用后，会偏移到下�?个匹�?
			result.add(matcher.group());// 获取当前匹配的�??
		}
		result.trimToSize();
		return result;
	}

	/**
	 * 获取第一个匹配正则表达式的子�?
	 * 
	 * @param str               完整字符�?
	 * @param reg               正则表达�?
	 * @param isCaseInsensitive 是否忽略大小写，true表示忽略，false表示大小写敏感�??
	 * @return 第一个匹配正则表达式的子串�??
	 */
	public static String getFirstMatch(final String str, final String reg, final boolean isCaseInsensitive) {
		Pattern pattern = null;
		if (isCaseInsensitive) {
			// 编译正则表达�?,忽略大小�?
			pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
		} else {
			// 编译正则表达�?,大小写敏�?
			pattern = Pattern.compile(reg);
		}
		Matcher matcher = pattern.matcher(str);// 指定要匹配的字符�?
		if (matcher.find()) {
			return matcher.group();
		}
		return null;
	}

	/**
	 * 从html代码中，获得指定标签的指定属性的取�??
	 * 
	 * @param html         HTML代码
	 * @param tagName      指定的标签名�?
	 * @param propertyName 指定的属性名�?
	 * @return
	 */
	public static final List<String> listTagPropertyValue(final String html, final String tagName,
			final String propertyName) {
		// 结果集合
		ArrayList<String> result = new ArrayList<String>();
		// 找出HTML代码中所有的tagName标签
		List<String> list = getMatchList(html, "<" + tagName + "[^>]*>", true);
		// 循环遍历每个标签字符串，找出其中的属性字符串,比如 src=....
		for (String tagStr : list) {
			// 去掉标签结尾�?/>，方便后�? src 属�?�的正则表达式�??
			// 这样可以适应 <video src=http://www.yourhost.com/xxx> 这样的标�?
			if (tagStr.endsWith("/>")) {
				tagStr = tagStr.substring(0, tagStr.length() - 2);
				tagStr = tagStr + " ";
			}
			// 去掉标签结尾�?>，方便后面匹配属性的正则表达式�??
			// 这样可以适应 <video src=http://www.yourhost.com/xxx> 这样的标�?
			else if (tagStr.endsWith(">")) {
				tagStr = tagStr.substring(0, tagStr.length() - 1);
				tagStr = tagStr + " ";
			}
			// 去掉字符串开头的 <video �? <source
			tagStr = tagStr.substring(1 + tagName.length());
			tagStr = " " + tagStr;

			// 取出属�?�的�?
			String regSingleQuote = "^" + propertyName + "='[^']*'"; // 使用单引�?
			String regDoubleQuote = "^" + propertyName + "=\"[^\"]*\""; // 使用双引�?
			String reg = "^" + propertyName + "=[^\\s]*\\s"; // 不使用引�?
			int index = 0;
			int length = tagStr.length();
			while (index <= length) {
				String subStr = tagStr.substring(index);
				String str = getFirstMatch(subStr, regSingleQuote, true);
				if (null != str) {
					// �?后跳过已经匹配的字符串�??
					index += str.length();
					String srcStr = str;
					srcStr = srcStr.trim();
					// 去掉 src=
					srcStr = srcStr.substring(propertyName.length() + 1);
					// 去掉单引�?
					srcStr = srcStr.substring(1);
					srcStr = srcStr.substring(0, srcStr.length() - 1);
					// 结果中加入图片URL
					result.add(srcStr);
				} else if ((str = getFirstMatch(subStr, regDoubleQuote, true)) != null) {
					// �?后跳过已经匹配的字符串�??
					index += str.length();
					String srcStr = str;
					srcStr = srcStr.trim();
					// 去掉 src=
					srcStr = srcStr.substring(propertyName.length() + 1);
					// 去掉双引�?
					srcStr = srcStr.substring(1);
					srcStr = srcStr.substring(0, srcStr.length() - 1);
					// 结果中加入图片URL
					result.add(srcStr);
				} else if ((str = getFirstMatch(subStr, reg, true)) != null) {
					// �?后跳过已经匹配的字符串�??
					index += str.length();
					String srcStr = str;
					srcStr = srcStr.trim();
					// 去掉 src=
					srcStr = srcStr.substring(propertyName.length() + 1);
					// 结果中加入图片URL
					result.add(srcStr);
				} else if ((str = getFirstMatch(subStr, "^[\\w]+='[^']*'", true)) != null) {
					// �?后跳过已经匹配的字符串�??
					index += str.length();
				} else {
					index++;
				}
			}
		} // end for (String tagStr : list)
		result.trimToSize();
		return result;
	}

	/**
	 * 从html代码中找出img标签的图片路�?
	 * 
	 * @param html HTML代码
	 * @return 字符串列表，里面每个字符串都是图片链接地�?
	 */
	public static List<String> listImgSrc(final String html) {
		return listTagPropertyValue(html, "img", "src");
	}
	
	public static void main(String[] args) {
        String html = "<p>欢迎回到万圣节无限畅饮变装超级音乐季派对�?</p><p>万圣前夜，万圣夜，万圣节三天为每个城市准备了六场好玩的派对，三个不同的万圣主题，保持�?纯正的味�?</p><p>中外超模DJ+气氛摇滚乐队+宝藏Rapper，还有超级容易邂逅彼此的游戏�?切都让你不虚此行�?</p><p><br></p><p><strong>万圣前夜 10�?30日�?</strong></p><p><strong>第一�? 百变�?心鬼 | 第二�? 魔仙�?</strong></p><p><br></p><p><strong>万圣夜 �?10�?31日�?</strong></p><p><strong>第一�? 百变�?心鬼 | 第二�? 魔仙�?</strong></p><p><br></p><p><strong>万圣节 �?11�?01日�?</strong></p><p><strong>第一�? 回魂夜 | 第二�? 回魂�?</strong></p><p><br></p><p><br></p><p><strong>（一） 万圣节·畅饮</strong></p><p style=\"text-align: center;\"><img src=\"https://p0.meituan.net/myvideodistribute/415b9494b08030c3add6bd0f709958d5881876.png\" style=\"max-width: 100%;\"></p><p>万圣节派对开始啦！各种场景布置，�?佳抖音�?�微博�?�朋友圈刷屏网红拍照基地，快来带小伙伴一起打卡吧�?</p><p style=\"text-align: center;\"><img src=\"https://p1.meituan.net/myvideodistribute/5d6547a4d69e733f1c0d03131185b072974903.png\" style=\"max-width: 100%;\"></p><p>我们保持传统酒水畅饮，新增红色吸�?特调，把全部感官交给MAO，用音乐填充耳朵，用酒精灌满胃口。洋酒，啤酒，饮料，只要你能喝，这里无限续杯，带走今夜最鬼魅的酒鬼回家，让你感受宿醉之后的快乐�??</p><p style=\"text-align: center;\"><img src=\"https://p0.meituan.net/myvideodistribute/74e66e8edef1e11537676b2978a2f7ad1011493.png\" style=\"max-width: 100%;\"></p><p><br></p><p><br></p><p><strong>（二） 万圣节·化妆舞会</strong></p><p style=\"text-align: center;\"><img src=\"https://p0.meituan.net/myvideodistribute/ed84ea42b34fd0fc0f8f77a0ad21088c955090.png\" style=\"max-width: 100%;\"></p><p>万圣节化妆舞会，活动当天到场，有专业化妆师为您化妆�?�骷髅妆、女巫妆、僵尸妆、小丑妆…�??</p><p><br></p><p>魔幻变装，让你与众不同，快来体验吧！我们期待您的到来�?</p><p><img src=\"https://p0.meituan.net/myvideodistribute/f98a6513cd08489ee0e02b423ea39bad693798.png\" style=\"max-width: 100%;\"></p><p><br></p><p><br></p><p><strong>（三）  万圣节·派对</strong></p><p style=\"text-align: center;\"><img src=\"https://p0.meituan.net/myvideodistribute/3e96a35e57a1063cc1b90330e19642d5522611.png\" style=\"max-width: 100%;\"></p><p>人人都爱派对，糖果将再次洒满天空，派对游戏将是我们必不可少的环节，特调僵尸对饮，我们将给予获胜�?�场地近期的任意门票給游戏之王，还有空气吉他比赛，让你成为最有样的鬼魅�??</p><p style=\"text-align: center;\"><img src=\"https://p0.meituan.net/myvideodistribute/e32fe1397d7efe1e5dd084fa4e0fbee2681596.png\" style=\"max-width: 100%;\"></p><p>当然我们核心还是音乐，这里有�?鬼魅的中外超模DJ，最氛围的乐队，�?宝藏的Rapper，让气氛濒临高峰，让这个世界�?直下沉，而我们在彻夜狂欢�?</p><p>请不要吝啬你的舞步和妆容，尽情摇摆好吗？！我们现场见�?</p><p><br></p><p><br></p><p><strong>（四）  阵�?</strong></p><p style=\"text-align: center;\"><strong style=\"color: rgb(192, 0, 0);\">10.30 | 19�?30 第一�?</strong></p><p><strong>DJ LiLi</strong></p><p><img src=\"https://p1.meituan.net/myvideodistribute/b1917fc763021c26bee1d262edba60db891116.png\" style=\"max-width: 100%;\"></p><p><br></p><p><strong>G乐团</strong></p><p><img src=\"https://p1.meituan.net/myvideodistribute/f45d634c13e4a50449ac68992e44a75e959196.png\" style=\"max-width: 100%;\"></p><p><br></p><p style=\"text-align: justify;\"><strong>Mc Nobi</strong></p><p><img src=\"https://p0.meituan.net/myvideodistribute/de3074d93c25eb0de3920ac1846844e7767250.png\" style=\"max-width: 100%;\"></p><p><br></p><p><br></p><p style=\"text-align: center;\"><strong style=\"color: rgb(192, 0, 0);\">10.30 | 22�?30 第二�?</strong></p><p><strong>DJ LiLi</strong></p><p><img src=\"https://p0.meituan.net/myvideodistribute/f3650ee6ff14c8befa3f3ab9ef735efb500505.png\" style=\"max-width: 100%;\"></p><p><strong style=\"color: rgb(255, 255, 255);\">纳米音壳 万圣前夜 第二�?</strong></p><p><strong style=\"color: rgb(0, 0, 0);\">纳米音壳</strong></p><p><img src=\"https://p0.meituan.net/myvideodistribute/7a0a473dec7385d25aadb79adad46751490019.png\" style=\"max-width: 100%;\"></p><p><br></p><p><strong>Wave Boi X Aioz</strong></p><p><img src=\"https://p0.meituan.net/myvideodistribute/fd609fc2a107b2d108908a54e55f12ca758712.png\" style=\"max-width: 100%;\"></p><p style=\"text-align: justify;\"><br></p><p style=\"text-align: justify;\"><br></p><p style=\"text-align: center;\"><strong style=\"color: rgb(192, 0, 0);\">10.31 | 19�?30 第一�?</strong></p><p><strong>DJ akira</strong></p><p><img src=\"https://p0.meituan.net/myvideodistribute/a8d632d116800d1614ccc01c43093f6e1299851.png\" style=\"max-width: 100%;\"></p><p><br></p><p><strong>苏菲花园</strong></p><p><img src=\"https://p0.meituan.net/myvideodistribute/9b6b2501d727759dec2d1bb22815fe15618530.png\" style=\"max-width: 100%;\"></p><p><br></p><p><strong style=\"color: rgb(0, 0, 0);\">卡卡</strong></p><p><img src=\"https://p0.meituan.net/myvideodistribute/ee65a12cc114a8e6afb1213762623c2f629447.png\" style=\"max-width: 100%;\"></p><p><br></p><p><strong style=\"color: rgb(0, 0, 0);\">Daniel& LB The Dragon，王昱尘</strong></p><p><br></p><p><img src=\"https://p1.meituan.net/myvideodistribute/2b0c7714a8e3b69b2bd97ad6c8af7cb0829367.png\" style=\"max-width: 100%;\"></p><p><br></p><p><br></p><p style=\"text-align: center;\"><strong style=\"color: rgb(192, 0, 0);\">10.31 | 22�?30 第二�?</strong></p><p><strong style=\"color: rgb(192, 0, 0);\">DJ akira</strong></p><p><br></p><p style=\"text-align: justify;\"><img src=\"https://p0.meituan.net/myvideodistribute/3c5139876cdd5cc2ce783b8941386067725866.png\" style=\"max-width: 100%;\"></p><p><br></p><p><strong>秘密俱乐�?</strong></p><p><img src=\"https://p0.meituan.net/myvideodistribute/4b41a079870e7df3f119c7cf493e475e522387.png\" style=\"max-width: 100%;\"></p><p><br></p><p><strong>C.S YEAR</strong></p><p><img src=\"https://p0.meituan.net/myvideodistribute/6741193bf5287bc4c9692b29245d1b97660659.png\" style=\"max-width: 100%;\"></p><p><br></p><p><strong>NightXdobj1，Young PacXA.Lee</strong></p><p><br></p><p><br></p><p><img src=\"https://p0.meituan.net/myvideodistribute/cd0f7fbb2e703e103968fc82233ec60b1357709.png\" style=\"max-width: 100%;\"></p><p><br></p><p style=\"text-align: center;\"><strong style=\"color: rgb(192, 0, 0);\">11.01 | 19�?30 第一�?</strong></p><p><strong>DJ LiLi </strong></p><p><img src=\"https://p0.meituan.net/myvideodistribute/53c88cd068a7982fadea8390fac2dce2716001.png\" style=\"max-width: 100%;\"></p><p><br></p><p><strong>领导先走</strong></p><p><img src=\"https://p0.meituan.net/myvideodistribute/1c8557143e98605810fbc2b0ca233f44974355.png\" style=\"max-width: 100%;\"></p><p><br></p><p><strong>囍�?��?</strong></p><p><img src=\"https://p0.meituan.net/myvideodistribute/6005f79e45504ca3a2deee7049433d991276205.png\" style=\"max-width: 100%;\"></p><p><br></p><p><strong>Nick </strong></p><p><img src=\"https://p0.meituan.net/myvideodistribute/358b6ff55f90ea0115660e532b373005826248.png\" style=\"max-width: 100%;\"></p><p><br></p><p><br></p><p style=\"text-align: center;\"><strong style=\"color: rgb(192, 0, 0);\">11.01 | 22�?30 第二�?</strong></p><p><strong>UNCLE_BAD坏叔�?</strong></p><p><img src=\"https://p1.meituan.net/myvideodistribute/8b832d0444d50588259088923bf00958444071.png\" style=\"max-width: 100%;\"></p><p><br></p><p><strong>怪兽计划 </strong></p><p><img src=\"https://p0.meituan.net/myvideodistribute/a7bf1c8c53e04481fad67a677b2875a9475450.png\" style=\"max-width: 100%;\"></p><p><br></p><p><strong>Bedlam疯人�?</strong></p><p><img src=\"https://p0.meituan.net/myvideodistribute/4480406c7888905098a5cc4d06491a50437886.png\" style=\"max-width: 100%;\"></p><p><br></p><p><br></p>";
        html=html.replaceAll("\\<img\\s*src=\"https\\://p\\d+.meituan.net", "<img src=\"http://d.musicapp.migu.cn/ticket");
        //html=html.replaceAll("https://p1.meituan.net", "http://d.musicapp.migu.cn/ticket");
       System.out.println(html);
        List<String> list = listImgSrc(html);
        for (String str : list) {
            System.out.println(str);
        }
    }
}
