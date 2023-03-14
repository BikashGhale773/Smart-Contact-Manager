console.log("This is script file")

const toggleSidebar = () => {
    if($(".sidebar").is(":visible")){
    //true then hide
        $(".sidebar").css("display", "none");
        $(".content").css("margin-left","0%");
    }else{
        //false then show
        $(".sidebar").css("display", "block");
        $(".content").css("margin-left","20%");
    }
};