 $('.select-value').mPicker({
        level:3,
        dataJson: city3,
        Linkage:true,
        rows:6,
        idDefault:true,
        splitStr:'-',
        header:'<div class="mPicker-header">三级联动选择插件</div>',
        confirm:function(json){
            console.info('当前选中json：',json);
            console.info('【json里有不带value时】');
            console.info('选中的id序号为：', json.ids);
            console.info('选中的value为：', json.values);
            // var id1= $('.select-value').data('id1');
            // var id2 = $('.select-value').data('id2');
            // var id3 = $('.select-value').data('id3');
            // console.info('第一列json：',city3[id1]);
            // console.info('第二列json：', city3[id1].child[id2]);
            // console.info('第三列json：', city3[id1].child[id2].child[id3]);
        },
        cancel:function(json){
            console.info('当前选中json：',json);
        }
    })


 
    //获取mpicker实例
    var method= $('.select-value').data('mPicker');
    console.info('第一个mpicker的实例为：',method);