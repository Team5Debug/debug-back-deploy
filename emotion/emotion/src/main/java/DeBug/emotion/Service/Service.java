package DeBug.emotion.Service;

import DeBug.emotion.Repository.MongoDB_Repository;
import DeBug.emotion.domain.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;

import java.util.Base64;
import java.util.List;

public class Service {
    @Value("${java.file.youtubeAPIKey}") // 변수 파일에 등록된 java.file.test 값 가져오기
    String youtubeAPIKey;

    @Value("${java.file.flask_ip}")
    String flask_ip;

    public Service(MongoDB_Repository mongoDB_Repository) {
        this.mongoDB_Repository = mongoDB_Repository;
    }

    private final MongoDB_Repository mongoDB_Repository;

    //jwt토큰 바디값 디코딩 받아오기
    public User getSubject(String token, String access_token) {
        String channels_Id = Channel_Id(access_token);
        //바디 디코딩 후 json형태로 변환
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String subject = new String(decoder.decode(token));
        JSONObject payload = new JSONObject(subject);
        //값 가져오기
        User user = new User();
        user.setChannels_Id(channels_Id);
        user.setName(payload.getString("name"));
        user.set_id(payload.getString("email"));
        //user.setLocale(payload.getString("locale"));
        user.setPicture(payload.getString("picture"));
        return mongoDB_Repository.insert_User(user);
    }

    //본인 인증 및 방송정보 저장
    public String identification(User user, String BCID) {

        //유저가 없으면 null반환
        if (user == null) return null;

        try {
            JSONObject json = get_YouTubeBC_Data(BCID);
            if (json == null) return null;
            //방송정보 담기
            BroadCast BC = new BroadCast();
            BC.set_id(BCID);
            String URI = "https://www.youtube.com/watch?v=" + BCID;
            BC.setURI(URI);
            BC.setTitle(json.getString("title"));
            BC.setThumbnailsUrl(json.getJSONObject("thumbnails").getJSONObject("default").getString("url"));
            BC.setUser(user);
            BC.setPublished(json.getString("publishedAt"));
            if (mongoDB_Repository.save_BroadCast(BC).equals("200")) {
                return BCID;
            }
            return "400";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "400";
        }
    }

    //채팅 저장
    public String chat(User user, Chat chat, String BCID, String name) {
        return mongoDB_Repository.chat(user, chat, BCID, name);
    }

    public Total_Data mypageData(User user) {
        return mongoDB_Repository.mypageData(user);
    }

    public FeedbackData getChat(String BCID) {
        return mongoDB_Repository.getChat(BCID);
    }


    //채널아이디 가져오기
    private String Channel_Id(String access_token) {
        String URI = "https://www.googleapis.com/youtube/v3/channels?part=snippet&mine=true";

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(URI);
        request.addHeader("accept", "application/json");
        request.setHeader("Authorization", "Bearer " + access_token);
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
                JSONObject a = new JSONObject(json.getJSONArray("items").get(0).toString());
                return a.getString("id");
            } catch (Exception e) {
                System.err.println("Error closing HttpClient: " + e.getMessage());
                return null;
            }
        }
    }

    //방송 정보 가져오기
    private JSONObject get_YouTubeBC_Data(String BCID) {
        String API_KEY = youtubeAPIKey;
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

    public String saveClass(Purchase_History PH) {
        return mongoDB_Repository.saveClass(PH);
    }

    public List<Purchase_History> getPurchaseHistory(String email) {
        return mongoDB_Repository.getPurchaseHistory(email);
    }

    public String saveViewer(String BCID, String sec, String viewer) {
        return mongoDB_Repository.saveViewer(BCID, sec, viewer);
    }

    public String testdata() {
        mongoDB_Repository.testdata();
        return "200";
    }

    //토픽
    public Topic getTopic(String BCID) {
        Topic topic = mongoDB_Repository.getTopic(BCID);
        if (topic == null) {
            String URI = flask_ip + "/feedback/" + BCID;

            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet request = new HttpGet(URI);
            request.addHeader("accept", "application/json");

            JSONObject json = new JSONObject();

            try {
                HttpResponse response = httpClient.execute(request);
                String jsonString = EntityUtils.toString(response.getEntity());
                json = new JSONObject(jsonString);
                topic = new Topic();

                Topic_data po = new Topic_data();
                Topic_data ne = new Topic_data();
                Topic_data emo7 = new Topic_data();

                topic.setPositive_topicData(po);
                topic.setNegative_topicData(ne);
                topic.setEmotion7_topicData(emo7);
                topic.setEmotion7(2);

                if (mongoDB_Repository.saveTopic(BCID, topic) == "400") {
                    return null;
                }
                return topic;

            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return null;
            }
        }
        return mongoDB_Repository.getTopic(BCID);
    }


}
