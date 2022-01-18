$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

/*	//发送异步请求AJAX请求之前，将csrf令牌存到请求的消息头
	var token = $("meta[name='_crsf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	$(document).ajaxSend(function (e, xhr, options) {
		xhr.setRequestHeader(header,token);
	});*/

	//获取标题和内容
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();
	//发送异步请求post
	$.post(
		CONTEXT_PATH+"/discuss/add",
		{"title":title,"content":content},
		function (data) {
			data = $.parseJSON(data);
			//在提示框显示返回消息
			$("#hintModal").text(data.msg);
			// //显示提示框
			$("#hintModal").modal("show");
			//两秒后自动隐藏
			setTimeout(function(){
				$("#hintModal").modal("hide");
				//刷新页面
					if(data.code == 0){
						window.location.reload();
					}
				}, 2000);
			}
	);


}