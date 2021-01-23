package getAPI;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.util.ArrayList;
import java.util.Map;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class HttpTest {
    public static void main(String[] args)throws Exception  {
        ArrayList<String> list = new ArrayList<>();
        String s1 = getHttp("https://covid-api.mmediagroup.fr/v1/cases?country=China");
        String s2 = getHttp("https://covid-api.mmediagroup.fr/v1/cases?country=US");
        String s3 = getHttp("https://covid-api.mmediagroup.fr/v1/cases?country=United%20Kingdom");
        String s4 = getHttp("https://covid-api.mmediagroup.fr/v1/cases?country=Japan");
        list.add(s1);
        list.add(s2);
        list.add(s3);
        list.add(s4);
        Connection connection =getConnection();
        Statement state = connection.createStatement();
        for (int i = 0; i < 4; i++) {   //向表填入数据
            Object temp = list.get(i);
            String str1 = String.valueOf(temp);
            Map<String, Object> jsonMap = JSONObject.parseObject(str1);
            String str2 = String.valueOf(jsonMap.get("All"));
            Map<String, Object> jsonSecondMap = JSONObject.parseObject(str2);
            String country = String.valueOf(jsonSecondMap.get("country"));
            String confirmed = String.valueOf(jsonSecondMap.get("confirmed"));
            String recovered = String.valueOf(jsonSecondMap.get("recovered"));
            String deaths = String.valueOf(jsonSecondMap.get("deaths"));
            String sqlStr = String.format("INSERT INTO yiqing_data() VALUES ('%s',%s,%s,%s)", country, confirmed, recovered, deaths);
            state.executeUpdate(sqlStr);
        }
        if (state!=null)
            state.close();
        if(connection!=null)
            connection.close();
    }

    public static Connection getConnection(){
        Connection conn = null;
        try {
            //初始化驱动类com.mysql.jdbc.Driver
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/yiqing_data?characterEncoding=UTF-8&useSSL=false","root", "cyz2952178079");
            //该类就在 mysql-connector-java-5.0.8-bin.jar中,如果忘记了第一个步骤的导包，就会抛出ClassNotFoundException
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
    public static String getHttp(String url) throws Exception {
        //创建httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //创建Http get请求
        HttpGet httpGet1 = new HttpGet(url);
        //接收返回值
        CloseableHttpResponse response = null;
        String content="";
        try {
            //请求执行
            response = httpClient.execute(httpGet1);
            if (response.getStatusLine().getStatusCode() == 200) {
                content = EntityUtils.toString(response.getEntity(), "utf-8");
                return "--------->"+content;
            }
        } finally {
            if (response != null) {
                response.close();
            }
            httpClient.close();
            return "--------->"+content;
        }
    }

}