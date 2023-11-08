package DeBug.emotion.Service;

import DeBug.emotion.Repository.MongoDB_Repository;
import DeBug.emotion.domain.BroadCast;
import DeBug.emotion.domain.Chat;
import DeBug.emotion.domain.Total_Data;
import DeBug.emotion.domain.User;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.Base64;

public class User_Service {

    public User_Service(MongoDB_Repository mongoDB_Repository) {
        this.mongoDB_Repository = mongoDB_Repository;
    }

    private final MongoDB_Repository mongoDB_Repository;

    //jwt토큰 바디값 디코딩 받아오기
    public User getSubject(String token) {
        //바디 디코딩 후 json형태로 변환
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String subject = new String(decoder.decode(token));
        JSONObject payload = new JSONObject(subject);

        //값 가져오기
        User user = new User();
        user.setName(payload.getString("name"));
        user.set_id(payload.getString("email"));
        //user.setLocale(payload.getString("locale"));
        user.setPicture(payload.getString("picture"));
        return mongoDB_Repository.insert_User(user);
    }

    //본인 인증 및 방송정보 저장
    public String identification(User user, String URI, String BCID) {

        //유저가 없으면 null반환
        if (user == null) return null;

        try {
            JSONObject json = get_YouTubeBC_Data(BCID);
            if(json ==null) return null;
            //방송정보 담기
            BroadCast BC = new BroadCast();
            BC.set_id(BCID);
            BC.setURI(URI);
            BC.setTitle(json.getString("title"));
            BC.setThumbnailsUrl(json.getJSONObject("thumbnails").getJSONObject("default").getString("url"));
            BC.setUser(user);
            return mongoDB_Repository.save_BroadCast(BC);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return "400";
        }
    }

    //채팅 저장
    public String chat(User user,Chat chat,String BCID,String name){
        return mongoDB_Repository.chat(user,chat, BCID,name);
    }

    public Total_Data test(User user){
        return mongoDB_Repository.test(user);
    }

    //방송 정보 가져오기
    private JSONObject get_YouTubeBC_Data(String BCID) {
        String API_KEY = "AIzaSyATIpI3znMnt3r9-9N3zr6ijtqV8ySiPwQ";
        String URI = "https://www.googleapis.com/youtube/v3/videos?id=" + BCID +
                "&key=" + API_KEY + "&part=snippet,contentDetails,statistics,status";

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(URI);
        request.addHeader("accept", "application/json");
        JSONObject json = new JSONObject();

        try {
            HttpResponse response = httpClient.execute(request);
            String jsonString = EntityUtils.toString(response.getEntity());
            json = new JSONObject(jsonString);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return null;
        } finally {
            try {
                httpClient.close();
                return new JSONObject(json.getJSONArray("items").get(0).toString()).getJSONObject("snippet");
            } catch (Exception e) {
                System.err.println("Error closing HttpClient: " + e.getMessage());
                return null;
            }
        }
    }

}
