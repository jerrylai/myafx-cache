package cn.myafx.cache;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.*;

import org.w3c.dom.*;

/**
 * 缓存key配置
 */
public class CacheKey implements ICacheKey {

    private ArrayList<CacheKeyConfig> list;

    /**
     * env:xmlCacheKeyFile or src/main/resources/cache-key.xml
     * 
     * @throws Exception
     */
    public CacheKey() throws Exception {
        String xmlFile = System.getenv("xmlCacheKeyFile");
        if (xmlFile == null || xmlFile.isEmpty()) {
            xmlFile = "src/main/resources/cache-key.xml";
        }

        this.load(xmlFile);
    }

    /**
     * 初始化
     * 
     * @param xmlFile
     */
    public CacheKey(String xmlFile) throws Exception {
        if (xmlFile == null || xmlFile.isEmpty())
            throw new Exception("cache-key.xml is not found!");

        String path = xmlFile;
        if (xmlFile.startsWith("classpath:")) {
            path = "src/main/resources/" + xmlFile.substring("classpath:".length());
        } else if (xmlFile == "env:xmlCacheKeyFile") {
            path = System.getenv("xmlCacheKeyFile");
        }

        this.load(path);
    }

    /**
     * CacheKey
     * 
     * @param stream cache-key.xml
     * @throws Exception
     */
    public CacheKey(InputStream stream) throws Exception {
        this.load(stream);
    }

    private static List<Integer> getDbList(String val) {
        ArrayList<Integer> list = null;
        if (val != null && !val.isEmpty()) {
            list = new ArrayList<>();
            String[] arr = val.split(",");
            for (int i = 0; i < arr.length; i++) {
                String ss = arr[i].trim();
                if (ss != null && !ss.isEmpty()) {
                    if (ss.contains("-")) {
                        String[] ssarr = ss.split("-");
                        if (ssarr.length == 2) {
                            String bs = ssarr[0].trim();
                            String es = ssarr[1].trim();
                            int bv = 0;
                            int ev = 0;
                            try {
                                bv = Integer.parseInt(bs);
                                ev = Integer.parseInt(es);
                                if (bv <= ev) {
                                    while (bv < ev) {
                                        list.add(bv++);
                                    }
                                    list.add(ev);
                                }
                            } catch (Exception ex) {
                            }
                        }
                    } else {
                        try {
                            int v = Integer.parseInt(ss);
                            list.add(v);
                        } catch (Exception ex) {
                        }
                    }
                }
            }
            list.trimToSize();
        }

        return list;
    }

    private static Integer parseExpire(String str) {
        Integer expire = null;
        if (str != null && !str.isEmpty()) {
            var arr = str.split(":");
            for (var j = 0; j < arr.length / 2; j++) {
                var v = arr[arr.length - j - 1];
                arr[arr.length - j - 1] = arr[j];
                arr[j] = v;
            }
            try {
                // 秒
                Integer v = Integer.parseInt(arr[0]);
                // 分钟
                if (arr.length > 1)
                    v += Integer.parseInt(arr[1]) * 60;
                // 小时
                if (arr.length > 2)
                    v += Integer.parseInt(arr[2]) * 60 * 60;
                // 天
                if (arr.length > 3)
                    v += Integer.parseInt(arr[3]) * 60 * 60 * 24;
                expire = v;
            } catch (Exception ex) {
            }
        }

        return expire;
    }

    private void load(Document doc) throws Exception {
        var rootElement = doc.getDocumentElement();
        var nodes = rootElement.getChildNodes();
        this.list = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            if (!(n instanceof Element node))
                continue;
            List<Integer> node_db = getDbList(node.getAttribute("db"));
            if (node_db == null)
                node_db = new ArrayList<>(0);
            Integer node_expire = parseExpire(node.getAttribute("expire"));
            NodeList child = node.getChildNodes();
            for (int j = 0; j < child.getLength(); j++) {
                Node in = child.item(j);
                if (!(in instanceof Element item))
                    continue;
                var key = item.getAttribute("key");
                var db = getDbList(item.getAttribute("db"));
                if (db == null)
                    db = node_db;
                var expire = parseExpire(item.getAttribute("expire"));
                if (expire == null)
                    expire = node_expire;
                this.list.add(new CacheKeyConfig(node.getNodeName(), item.getNodeName(), key, expire, db));
            }
        }
        this.list.trimToSize();
    }

    private void load(String xmlFile) throws Exception {
        File f = new File(xmlFile);
        if (!f.exists() || !f.isFile())
            throw new Exception("xmlFile(" + xmlFile + ") not found!");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(f);
        this.load(doc);
    }

    private void load(InputStream stream) throws Exception {
        if (stream == null)
            throw new Exception("stream is null!");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(stream);
        this.load(doc);
    }

    /**
     * get
     * 
     * @param node db节点
     * @param item 节点名称
     * @return
     */
    @Override
    public CacheKeyConfig get(String node, String item) {
        CacheKeyConfig m = null;
        for (var q : this.list) {
            if (q.Node == node && q.Item == item) {
                m = q;
                break;
            }
        }

        return m;
    }

    /**
     * 获取key
     * 
     * @param node 节点
     * @param item 名称
     * @return key
     */
    @Override
    public String getKey(String node, String item) {
        var m = this.get(node, item);
        return m != null ? m.Key : null;
    }

    /**
     * 获取过期时间, 秒
     * 
     * @param node 节点
     * @param item 名称
     * @return 过期时间, 秒
     */
    @Override
    public Integer getExpire(String node, String item) {
        var m = this.get(node, item);
        return m != null ? m.Expire : null;
    }

    /**
     * 获取db
     * 
     * @param node 节点
     * @param item 名称
     * @return db list
     */
    @Override
    public List<Integer> getDb(String node, String item) {
        var m = this.get(node, item);
        return m != null ? m.Db : null;
    }
}
