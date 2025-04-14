package com.application.foodhub.chat;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/chat/private")
public class PrivateChatController {

    @Autowired
    private ChatRoomService chatRoomService;

//    @GetMapping("/list")
//    @ResponseBody
//    public List<PrivateChatRoomDTO> getMyChatRooms(HttpSession session) {
//        String myId = (String) session.getAttribute("userId");
//        return chatRoomService.getPrivateChatRooms(myId);
//    }

//    @PostMapping("/create")
//    @ResponseBody
//    public PrivateChatRoomDTO createRoom(@RequestBody Map<String, String> request, HttpSession session) {
//        String myId = (String) session.getAttribute("userId");
//        String targetNickname = request.get("nickname");
//        return chatRoomService.createOrFindRoom(myId, targetNickname);
//    }
//
//    @GetMapping("/room/{roomId}")
//    public String enterRoom(@PathVariable Long roomId, Model model) {
//        model.addAttribute("roomId", roomId);
//        return "foodhub/chat/chatPrivate";  // 채팅 페이지로 이동
//    }
}