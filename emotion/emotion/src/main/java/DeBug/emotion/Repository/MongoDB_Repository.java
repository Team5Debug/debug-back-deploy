package DeBug.emotion.Repository;

import DeBug.emotion.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Transactional
public class MongoDB_Repository {

    @Autowired
    private User_Repository mongoDBUserRepository;

    @Autowired
    private BroadCast_Repository mongoDBBroadCastRepository;

    @Autowired
    private Author_Repositoy mongoDBAuthorRepository;

    @Autowired
    private Year_Repositoy mongoDBYearRepositoy;
    @Autowired
    private Purchase_History_Repository mongoPurchaseHistoryRepository;


    //회원정보가 없으면 db저장
    public User insert_User(User user) {

        User u = mongoDBUserRepository.findOneBy_id(user.get_id());
        if (u == null || u.getDate() == null || u.getDate().isBefore(LocalDateTime.now())) {
            user.setClass_name("베이직");
            user.setDate(null);
        } else {
            user.setClass_name(u.getClass_name());
            user.setDate(u.getDate());
        }
        mongoDBUserRepository.save(user);
        return user;
    }

    //유저의 방송 정보 저장
    public String save_BroadCast(BroadCast BC) {
        //방송정보 저장
        try {
            BroadCast TMP = mongoDBBroadCastRepository.findOneBy_id(BC.get_id());
            if (TMP != null) {
                return "200";
            }
            mongoDBBroadCastRepository.save(BC);
            return "200";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "400";
        }
    }

    //채팅 저장
    public String chat(User user, Chat chat, String BCID, String author_name) {

        //날짜 년,월,일 자르기
        String[] date = chat.getDateTime().split("-| ");
        String[] time = date[3].split(":");

        //유저 정보로 년 정보 찾기
        List<YearTotalData> yearList = mongoDBYearRepositoy.findByUser(user);
        YearTotalData yearTotalData = new YearTotalData();
        yearTotalData.set_id(date[0] + user.get_id());
        yearTotalData.setUser(user);
        LocalDate now = LocalDate.now();

        String cury = now.getYear() + user.get_id();

        for (int i = 0; i < yearList.size(); i++) {
            if (yearList.get(i).get_id().equals(cury)) {
                yearTotalData = yearList.get(i);
                yearTotalData.setUser(user);
                break;
            }
        }

        yearTotalData.All_Emotion3[chat.getEmotion3()]++;
        yearTotalData.All_Emotion7[chat.getEmotion7()]++;
        int month = Integer.parseInt(date[1]) - 1;
        int day = Integer.parseInt(date[2]) - 1;
        if (yearTotalData.monthTotalData[month] == null) {
            yearTotalData.monthTotalData[month] = new MonthTotalData();
        }
        yearTotalData.monthTotalData[month].getAll_Emotion3()[chat.getEmotion3()]++;
        yearTotalData.monthTotalData[month].getAll_Emotion7()[chat.getEmotion7()]++;
        if (yearTotalData.monthTotalData[month].getDay_total_data()[day] == null) {
            yearTotalData.monthTotalData[month].getDay_total_data()[day] = new DayTotalData();
        }
        yearTotalData.monthTotalData[month].getDay_total_data()[day].getAll_Emotion3()[chat.getEmotion3()]++;
        yearTotalData.monthTotalData[month].getDay_total_data()[day].getAll_Emotion7()[chat.getEmotion7()]++;

        int hour = Integer.parseInt(time[0]) - 1;

        if (yearTotalData.monthTotalData[month].getDay_total_data()[day].
                One_Hour_Emotion[hour] == null) {
            yearTotalData.monthTotalData[month].getDay_total_data()[day].
                    One_Hour_Emotion[hour] = new HourData();
        }
        yearTotalData.monthTotalData[month].getDay_total_data()[day].
                One_Hour_Emotion[hour].All_Emotion3[chat.getEmotion3()]++;
        yearTotalData.monthTotalData[month].getDay_total_data()[day].
                One_Hour_Emotion[hour].All_Emotion7[chat.getEmotion7()]++;

        yearTotalData.setUser(user);
        mongoDBYearRepositoy.save(yearTotalData);

        //방송 정보 저장
        BroadCast BC = mongoDBBroadCastRepository.findById(BCID).get();
        BC.All_Emotion3[chat.getEmotion3()]++;
        BC.All_Emotion7[chat.getEmotion7()]++;
        mongoDBBroadCastRepository.save(BC);


        //방송 정보로 시청자 정보 가져오기
        BroadCast sampleBC = new BroadCast();
        sampleBC.set_id(BCID);
        List<Author> authorList = mongoDBAuthorRepository.findByBroadCast(sampleBC);

        //시청자 채팅 저장
        for (int i = 0; i < authorList.size(); i++) {
            Author author = authorList.get(i);
            if (author.getName().equals(author_name)) {
                return save_chat(author, chat);
            }
        }

        Author author = new Author();
        author.setBroadCast(sampleBC);
        author.setName(author_name);
        return save_chat(author, chat);
    }

    public Total_Data mypageData(User user) {
        //사용자의 정보들을 담을 객체
        Total_Data td = new Total_Data();
        List<BroadCast> bc = mongoDBBroadCastRepository.findByUser(user);
        List<YearTotalData> year = mongoDBYearRepositoy.findByUser(user);
        Collections.sort(year, (o1, o2) -> Integer.parseInt(o1.get_id().substring(0, 4)) - Integer.parseInt(o2.get_id().substring(0, 4)));
        td.setBroadCasts(bc);
        td.setYears(year);
        return td;
    }

    //정보 저장
    private String save_chat(Author author, Chat chat) {
        author.chat.add(chat);
        author.All_Emotion3[chat.getEmotion3()]++;
        author.All_Emotion7[chat.getEmotion7()]++;
        try {
            mongoDBAuthorRepository.save(author);
            return "200";
        } catch (Exception e) {
            System.out.println("채팅 저장 실패");
            return "400";
        }
    }


    //결제 정보 저장
    public String saveClass(Purchase_History PH) {
        try {
            User user = mongoDBUserRepository.findOneBy_id(PH.getUser().get_id());

            //받아온 정보 저장
            PH.setEnd_date(PH.getStart_date().plusMonths(1));
            user.setDate(PH.getStart_date().plusMonths(1));
            user.setClass_name(PH.getName());

            mongoPurchaseHistoryRepository.insert(PH);
            mongoDBUserRepository.save(user);

            return "200";
        } catch (Exception e) {
            return "400";
        }
    }

    //결제정보
    public List<Purchase_History> getPurchaseHistory(String email) {
        User user = new User();
        user.set_id(email);
        List<Purchase_History> list = mongoPurchaseHistoryRepository.findByUser(user);
        Collections.reverse(list);
        return list;
    }

    //시청자 수 저장
    public String saveViewer(String BCID, String sec, String viewer) {
        try {
            BroadCast BC = mongoDBBroadCastRepository.findOneBy_id(BCID);
            BC.Viewer.put(sec, Integer.parseInt(viewer));

            mongoDBBroadCastRepository.save(BC);
            return "200";
        } catch (Exception e) {
            System.out.println("error");
            return "400";
        }
    }

    //채팅데이터 반환
    public FeedbackData getChat(String BCID) {

        BroadCast BC = mongoDBBroadCastRepository.findOneBy_id(BCID);
        FeedbackData FD = new FeedbackData();
        FD.setPublished(BC.getPublished());
        FD.setViewer(BC.getViewer());
        List<Author> Authors = mongoDBAuthorRepository.findByBroadCast(BC);
        for (Author Author : Authors) {
            for (Chat chat : Author.getChat()) {
                FD.cd.add(chat);
            }
        }
        return FD;
    }

    //방송아이디로 방송정보 가져오고 토픽 정보 확인
    public Topic getTopic(String BCID) {
        try {
            BroadCast BC = mongoDBBroadCastRepository.findOneBy_id(BCID);
            Topic topic = BC.getTopic();
            return topic;
        } catch (Exception e) {
            return null;
        }
    }

    public String saveTopic(String BCID, Topic topic) {
        try {
            BroadCast BC = mongoDBBroadCastRepository.findOneBy_id(BCID);
            BC.setTopic(topic);
            mongoDBBroadCastRepository.save(BC);
            return "200";
        } catch (Exception e) {
            return "400";
        }
    }


    public String testdata() {
        int[] a = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        YearTotalData y = new YearTotalData();
        y.set_id("2022dbsruaqls123@gmail.com");
        User user = new User();
        user.set_id("dbsruaqls123@gmail.com");
        y.setUser(user);


        for (int i = 3; i < 12; i++) {
            y.getMonthTotalData()[i] = new MonthTotalData();
            for (int j = 0; j < a[i]; j++) {
                y.getMonthTotalData()[i].day_total_data[j] = new DayTotalData();
                for (int q = 0; q < 24; q++) {
                    y.getMonthTotalData()[i].day_total_data[j].One_Hour_Emotion[q] = new HourData();
                    for (int k = 0; k < 3; k++) {
                        int data = (int) (Math.random() * 30);
                        y.getMonthTotalData()[i].day_total_data[j].One_Hour_Emotion[q].All_Emotion3[k] = data;
                        y.getMonthTotalData()[i].day_total_data[j].All_Emotion3[k] += data;
                        y.getMonthTotalData()[i].All_Emotion3[k] += data;
                        y.All_Emotion3[k] += data;
                    }
                    for (int k = 0; k < 7; k++) {
                        int data = (int) (Math.random() * 30);
                        y.getMonthTotalData()[i].day_total_data[j].One_Hour_Emotion[q].All_Emotion7[k] = data;
                        y.getMonthTotalData()[i].day_total_data[j].All_Emotion7[k] += data;
                        y.getMonthTotalData()[i].All_Emotion7[k] += data;
                        y.All_Emotion7[k] += data;
                    }
                }
            }
        }
        mongoDBYearRepositoy.save(y);

        return "200";
    }


    //시청자 정보 저장
//    private Author find_AuthorByname(String name){
//        Author sampleAuthor = new Author();
//        sampleAuthor.setName(name);
//        Example<Author> example = Example.of(sampleAuthor);
//        Optional<Author> a  = mongoDBAuthorRepository.findOne(example);
//        try {
//            if (a.isEmpty()) {
//                return null;
//            }
//            return a.get();
//        }catch(Exception e){
//            return null;
//        }
//    }
}
