layui.use(['form', 'laydate', 'element', 'jquery'], function(){
	// var form = layui.form,
	var layer = layui.layer,
	$ = layui.jquery;
	// laydate = layui.laydate,
	// element = layui.element, 
    
    
	function loadGradeOne () {
		$.ajax({
			url: gradeKindUrl,
			type: 'POST',
			data: {
				stores_id: stores_id,
                oauth_token: oauth_token
			},
			success: function (res) {
              
			}
		})
	}
	$(".merchandise_control h1").click(function () {
        	loadGradeOne()
	})
});