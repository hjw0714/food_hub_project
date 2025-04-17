'use strict';

var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');

var stompClient = null;
var username = null;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {
    username = document.querySelector('#name').value.trim();

    if(username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}


function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/public', onMessageReceived);

    // Tell your username to the server
    stompClient.send("/app/chat.addUser",
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    )

    connectingElement.classList.add('hidden');
}


function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if(messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT'
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('li');

    if(message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' 반갑습니다!';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' 나가셨습니다!';
    } else {
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

	console.log(messageArea);
    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}


function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

usernameForm.addEventListener('submit', connect, true)
messageForm.addEventListener('submit', sendMessage, true)



document.querySelectorAll('.username-submit')[1].addEventListener('click', function (e) {
    e.preventDefault();
    document.getElementById("username-page").classList.add("hidden");
    document.getElementById("private-chat-page").classList.remove("hidden");
    loadMyPrivateChatRooms();
});

function loadMyPrivateChatRooms() {
    $.ajax({
        url: "/foodhub/chat/private/list",
        method: "GET",
        success: function(data) {
            const list = $('#myChatRoomList');
            list.empty();
            data.forEach(room => {
                const listItem = $(`
                    <li>
                        <button onclick="enterChatRoom(${room.roomId})">${room.otherUserNickname}님과의 대화방</button>
						<button onclick="deleteChatRoom('${room.roomId}')" class="delete-btn">삭제</button>
                    </li>
                `);
                list.append(listItem);
            });
        }
    });
}


// ===== 비공개 채팅방 생성 요청 =====
$('#searchBtn').on('click', function () {
    const nickname = $('#searchNickname').val();
    $.ajax({
        url: "/foodhub/chat/private/create",
        method: "POST",
        contentType: "application/json",
        data: JSON.stringify({ nickname: nickname }),
        success: function (data) {
            enterChatRoom(data.roomId);
        }
    });
});

// ===== 비공개 채팅방 입장 =====
function enterChatRoom(roomId) {
    $('#username-page').addClass('hidden');
    $('#private-chat-page').addClass('hidden');
    $('#private-message-page').removeClass('hidden');

    $.ajax({
        url: `/chat/private/messages/${roomId}`,
        method: "GET",
        success: function (messages) {
            const area = $('#privateMessageArea');
            area.empty();

            messages.forEach(message => {
                const messageElement = document.createElement('li');
                messageElement.classList.add('chat-message');

                const avatarElement = document.createElement('i');
                const avatarText = document.createTextNode(message.sender?.charAt(0) || '?');
                avatarElement.appendChild(avatarText);
                avatarElement.style['background-color'] = getAvatarColor(message.sender);

                const usernameElement = document.createElement('span');
                usernameElement.appendChild(document.createTextNode(message.sender));

                const textElement = document.createElement('p');
                textElement.appendChild(document.createTextNode(message.chatContent));

                messageElement.appendChild(avatarElement);
                messageElement.appendChild(usernameElement);
                messageElement.appendChild(textElement);

                area.append(messageElement);
            });

            area[0].scrollTop = area[0].scrollHeight;
        }
    });

    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function () {
        stompClient.subscribe(`/topic/private.${roomId}`, onPrivateMessageReceived);

        $('#privateSendBtn').off('click').on('click', function () {
            const messageContent = $('#privateMessageInput').val().trim();
            if (messageContent) {
                const chatMessage = {
					sender: $('#userId').val(),         // 서버에서 쓸 userId
				    senderNickname: $('#name').val(),   // 화면 표시용
                    content: messageContent,
                    type: 'CHAT'
                };
                stompClient.send(`/app/chat.private.${roomId}`, {}, JSON.stringify(chatMessage));
                $('#privateMessageInput').val('');
            }
        });
    });
	
	
	$('#privateMessageInput').off('keypress').on('keypress', function (e) {
	        if (e.which === 13) {
	            e.preventDefault();
	            $('#privateSendBtn').click();
	        }
	    });
}

// ===== 비공개 채팅 메시지 수신 =====
function onPrivateMessageReceived(payload) {
    const message = JSON.parse(payload.body);
    const messageElement = document.createElement('li');
    messageElement.classList.add('chat-message');

    const avatarElement = document.createElement('i');
    const avatarText = document.createTextNode(message.sender[0]);
    avatarElement.appendChild(avatarText);
    avatarElement.style['background-color'] = getAvatarColor(message.sender);

	const usernameElement = document.createElement('span');
	usernameElement.appendChild(document.createTextNode(message.senderNickname || message.sender));

    const textElement = document.createElement('p');
    textElement.appendChild(document.createTextNode(message.content));

    messageElement.appendChild(avatarElement);
    messageElement.appendChild(usernameElement);
    messageElement.appendChild(textElement);

    $('#privateMessageArea').append(messageElement);
    document.getElementById("privateMessageArea").scrollTop = document.getElementById("privateMessageArea").scrollHeight;
}

let leaveSent = false;

$("#backFromPublicChatBtn").on("click", function () {
    if (stompClient && stompClient.connected && !leaveSent) {
        leaveSent = true;
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify({
            sender: username,
            type: 'LEAVE'
        }));
        stompClient.disconnect(() => {
            $("#chat-page").addClass("hidden");
            $("#username-page").removeClass("hidden");
            $("#messageArea").empty();
            leaveSent = false; // 다음에 다시 입장 가능하도록 초기화
        });
    }
});

   // 비공개 채팅방에서 돌아가기
   $("#backFromPrivateChatBtn").on("click", function () {
       // 채팅 메시지 영역 비우기
       $("#privateMessageArea").empty();

       // stompClient 연결 종료 및 객체 해제
       if (stompClient && stompClient.connected) {
           stompClient.disconnect(() => {
               console.log("Disconnected from private chat room");
           });
           stompClient = null;
       }

       // 화면 전환
	   $("#private-message-page").addClass("hidden");
	   $("#private-chat-page").removeClass("hidden");
	   
	   // 리스트 다시 로드 (선택 사항)
	      loadMyPrivateChatRooms(); 
   });

   
  // 비공개 채팅방 리스트 → 초기화면
$("#backFromPrivateListBtn").on("click", function () {
          $("#private-chat-page").addClass("hidden");
          $("#username-page").removeClass("hidden");
  	});

$("#backFromPublicChatBtn").on("click", function () {
       $("#chat-page").addClass("hidden");
       $("#username-page").removeClass("hidden");
   });
	
   
// 대화방 삭제
function deleteChatRoom(roomId) {
       if (!confirm("정말로 이 대화방을 삭제하시겠습니까?")) return;

       $.ajax({
           url: `/foodhub/chat/private/delete/${roomId}`,
           method: "POST",
           success: function() {
               alert("대화방이 삭제되었습니다.");
               loadMyPrivateChatRooms();
           },
           error: function() {
               alert("삭제 중 오류가 발생했습니다.");
           }
       });
   }

   
   
$("#goToMainBtn").on("click", function () {
	        window.location.href = "/foodhub";  // 메인 페이지 주소
	});