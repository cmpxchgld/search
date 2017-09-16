package solr.utils;

import org.apache.commons.beanutils.BeanUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 解析xml工具类
 */
public class ParseXmlUtils {

    public static <T> List<T> parse(String StrXML, Class<T> t) throws DocumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        List<T> retList = new ArrayList<T>();
        Document doc = DocumentHelper.parseText(StrXML);
        Element root = doc.getRootElement();
        Element datalist = root.element("datalist");
        for (@SuppressWarnings("rawtypes")
             Iterator it = datalist.elementIterator(); it.hasNext(); ) {
            Element data = (Element) it.next();
            Map<String, Object> curMap = new HashMap<String, Object>();
            for (@SuppressWarnings("rawtypes")
                 Iterator p = data.elementIterator(); p.hasNext(); ) {
                Element curP = (Element) p.next();
                curMap.put(curP.getName(), curP.getText());
            }
            T object = map2Bean(curMap, t);
            retList.add(object);
        }
        return retList;
    }

    public static <T> T map2Bean(Map<String, Object> map, Class<T> clazz) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        T instance = clazz.newInstance();
        BeanUtils.populate(instance, map);
        return instance;
    }
}
