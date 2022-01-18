$(function () {
    $("#topBtn").click(setTop);
    $("#essenceBtn").click(setEssence);
    $("#blockBtn").click(setBlock);
})
function like(btn, entityType, entityId, entityUserId,postId) {
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType":entityType, "entityId": entityId, "entityUserId":entityUserId,"postId":postId},
        function (data) {
            data = $.parseJSON(data);
            if(data.code === 0){
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus == 1 ?'已赞':'赞');
            }else{
                alert(data.msg);
            }
        }
    )
}
//置顶
function setTop() {
    var postId = $("#postId").val();
    $.post(
        CONTEXT_PATH + "/discuss/top",
        {"id":postId},
        function (data) {
            data = $.parseJSON(data);
            if(data.code === 0){
                $("#topBtn").attr("disabled", "disabled");
            }else{
                alert(data.msg);
            }
        }
    )
}
//加精
function setEssence() {
    var postId = $("#postId").val();
    $.post(
        CONTEXT_PATH + "/discuss/essence",
        {"id":postId},
        function (data) {
            data = $.parseJSON(data);
            if(data.code === 0){
                $("#essenceBtn").attr("disabled", "disabled");
            }else{
                alert(data.msg);
            }
        }
    )
}
//拉黑
function setBlock() {
    var postId = $("#postId").val();
    $.post(
        CONTEXT_PATH + "/discuss/block",
        {"id":postId},
        function (data) {
            data = $.parseJSON(data);
            if(data.code === 0){
                location.href = CONTEXT_PATH +"/index";
            }else{
                alert(data.msg);
            }
        }
    )
}