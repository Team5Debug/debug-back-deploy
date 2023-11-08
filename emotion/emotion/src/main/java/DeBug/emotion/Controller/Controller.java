package DeBug.emotion.Controller;

import DeBug.emotion.Service.Service;
import DeBug.emotion.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Slf4j
@CrossOrigin("*")
public class Controller {

    public Controller(Service userService) {
        this.userService = userService;
    }

    private final Service userService;

    //토큰 받아오기
    //POST
    @GetMapping("/find")
    public User find_User(@RequestParam("id_token") String idToken,
                          @RequestParam("access_token") String access_token) {
        //jwt PAYLOAD부분 추출
        String payload = idToken.split("[.]")[1];
        return userService.getSubject(payload, access_token);
    }

    //본인확인
    //POST
    @RequestMapping("/identification")
    public String identification(@RequestParam("email") String email,
                                 @RequestParam("URI") String URI) {

        User user = new User();
        user.set_id(email);
        //URI에서 BCID추출
        String BCID = URI.replace("https://", "").replace("www.", "")
                .replace("youtube.com/watch?v=", "");

        return userService.identification(user, BCID);
    }
   

    //채팅 저장 및 전달
    //POST
    @RequestMapping("/chat")
    public String chat(@RequestParam("email") String email,
                       @RequestBody Chat chat,
                       @RequestParam("BCID") String BCID,
                       @RequestParam("name") String name) {
        User user = new User();
        user.set_id(email);
        userService.chat(user, chat, BCID, name);
        return "200";
    }

    //마이페이지 데이터
    @GetMapping("/mypage")
    public Total_Data mypage(@RequestParam("email") String email) {
        User user = new User();
        user.set_id(email);
        return userService.mypageData(user);
    }

    //결제 정보 저장
    //POST
    @RequestMapping("/saveClass")
//    상품이름:name
//    결제금액:amount
//    주문번호:merchant_uid
//    카드승인번호:apply_num
    public String saveClass(@RequestParam("email") String email,
                            @RequestParam("name") String name,
                            @RequestParam("amount") String amount,
                            @RequestParam("merchant_uid") String merchant_uid,
                            @RequestParam("apply_num") String apply_num) {
        User user = new User();
        user.set_id(email);
        Purchase_History PH = new Purchase_History(apply_num,name,amount,merchant_uid);
        PH.setUser(user);

        return userService.saveClass(PH);
    }

    //구매내역 가져오기
    @GetMapping("/getPurchaseHistory")
    public List<Purchase_History> getPurchaseHistory(@RequestParam("email")String email){
        return userService.getPurchaseHistory(email);
    }

    //시청자수 저장
    //POST
    @RequestMapping("/saveViewer")
    public String saveViewer(@RequestParam("BCID")String BCID,
                             @RequestParam("sec")String sec,
                             @RequestParam("viewer")String viewer){
        return userService.saveViewer(BCID,sec,viewer);
    }

    @GetMapping("/getChat")
    public FeedbackData getChat(@RequestParam("BCID")String BCID){
        return userService.getChat(BCID);
    }

    @GetMapping("getTopic")
    public Topic getTopic(@RequestParam("BCID")String BCID){
        return userService.getTopic(BCID);
    }

    @GetMapping("/testdata")
    public String testdata() {

        userService.testdata();

        return "200";
    }

}
