package solr.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Ximenlv on 2017/9/13.
 */
public class Pingyin4j {

    public static void main(String[] args) {
        String pinyin = "";
        List<String> hardPinyins = getHardPinyins("单于");
        for (String hardPinyin : hardPinyins) {
            pinyin+=hardPinyin;
        }
        System.out.println(pinyin);
    }
    //@Test
    public void testpingying4j() {
        List<String> chanyu = getHardPinyins("单于");
        String pingyin = "";
        for (String s : chanyu) {
            pingyin+=s;
        }
            System.out.println(pingyin);
    }

    /**
     * 获取一段文字的所有拼音组合情况,以list<String>形式返回
     */
    public static List<String> getHardPinyins(String s) {
        if (s == null) {
            s = "";//null时处理，后边处理时报错
        }
        String[][] allPinyins = new String[s.length()][];//存放整个字符串的各个字符所有可能的拼音情况，如果非汉字则是它本身
        char[] words = s.toCharArray();//把这段文字转成字符数组
        for (int i = 0; i < words.length; i++) {
            allPinyins[i] = Pingyin4j.getAllPinyins(words[i]);//每个字符的所有拼音情况
        }
        String[] resultArray = Pingyin4j.recursionArrays(allPinyins, allPinyins.length, 0);//用递归，求出这个2维数组每行取一个数据组合起来的所有情况
        return Arrays.asList(resultArray);//返回数组支持的固定大小的list(asList注意事项详见我的其他博客，可new LinkedList<String>(Arrays.asList()))来实现对结果随意操作
    }

    /**
     * 获取包含一个字符的拼音（多音字则以数组形式返回多个）,非汉字则返回字符本身
     */
    public static String[] getAllPinyins(char word) {
        HanyuPinyinOutputFormat pinyinFormat = new HanyuPinyinOutputFormat();   //创建拼音输入格式类
        pinyinFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);//指定格式中的大小写属性为小写
        pinyinFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);//指定音标格式无音标
        //pinyinFormat.setVCharType(HanyuPinyinVCharType.WITH_V);//指定用v表示ü
        String[] formatPinyin = null;
        try {
            formatPinyin = PinyinHelper.toHanyuPinyinStringArray(word, pinyinFormat);//获取对应的汉字拼音，不是汉字返回null
        } catch (BadHanyuPinyinOutputFormatCombination e) {//会抛出异常，捕获异常
            //logger.error(e.getMessage());
            e.printStackTrace();
        }
        if (formatPinyin == null) {
            formatPinyin = new String[1];
            formatPinyin[0] = String.valueOf(word);//返回读音,如果多音字自返回一个
        }
        return formatPinyin;
    }

    /**
     * 用递归方法，求出这个二维数组每行取一个数据组合起来的所有情况，返回一个字符串数组
     *
     * @param s      求组合数的2维数组
     * @param len    此二维数组的长度，省去每一次递归长度计算的时间和空间消耗，提高效率
     * @param cursor 类似JDBC、数据库、迭代器里的游标，指明当前从第几行开始计算求组合数，此处从0开始（数组第一行）
     *               避免在递归中不断复制剩余数组作为新参数，提高时间和空间的效率
     * @return String[] 以数组形式返回所有的组合情况
     */
    public static String[] recursionArrays(String[][] s, int len, int cursor) {
        if (cursor <= len - 2) {//递归条件,直至计算到还剩2行
            int len1 = s[cursor].length;
            int len2 = s[cursor + 1].length;
            int newLen = len1 * len2;//上下2行的总共的组合情况
            String[] temp = new String[newLen];//存上下2行中所有的组合情况
            int index = 0;
            for (int i = 0; i < len1; i++) {//嵌套循环遍历，求出上下2行中，分别取一个数据组合起来的所有情况
                for (int j = 0; j < len2; j++) {
                    temp[index] = s[cursor][i] + s[cursor + 1][j];
                    index++;
                }
            }
            s[cursor + 1] = temp;//把当前计算到此行的所有组合结果放在cursor+1行
            cursor++;//游标指向下一行，即上边的数据结果
            return Pingyin4j.recursionArrays(s, len, cursor);
        } else {
            return s[len - 1];//返回最终的所有组合结果
        }
    }
}
