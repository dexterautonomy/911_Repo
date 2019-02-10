/*Started 3rd Jan. 2019 8:00pm*/

$(document).ready(appFunction);

function appFunction()
{
    var1 = false;
    var2 = false;
    var3 = false;
    
    function showButton()
    {
        if(var1 && var2 && var3)
        {
            $('#signup').removeClass('hidden');
        }
    }
    
    function hideButton()
    {
        $('#signup').addClass('hidden');
    }
    
    $('#userName').focusout(function (event){
        var sentData = {username: $(this).val()};
        $.get('ajaxUsernameCheck', sentData, function(receivedData){
            
            switch (receivedData)
            {
                case "":
                {
                    var1 = true;
                    $('#info1').text('');
                    showButton();
                }
                break;
                
                case "username exists":
                {
                    var1 = false;
                    $('#info1').text('Username already exists').removeClass('blueColor').addClass('redColor');
                    hideButton();
                }
                break;
                
                case "invalid entry":
                {
                    var1 = false;
                    $('#info1').text('Username has invalid character(s)').removeClass('blueColor').addClass('redColor');
                    hideButton();
                }
                break;
            }
        });
        event.stopPropagation();
    });
    
    $('#email').focusout(function (event){
        var sentData = {email: $(this).val()};
        $.get('ajaxEmailCheck', sentData, function(receivedData){
            
            switch (receivedData)
            {
                case "":
                {
                    var2 = true;
                    $('#info2').text('');
                    showButton();
                }
                break;
                
                case "email exists":
                {
                    var2 = false;
                    $('#info2').text('Email already exists').removeClass('blueColor').addClass('redColor');
                    hideButton();
                }
                break;
            }
        });
        event.stopPropagation();
    });
    
    $('#password').focusout(function (event){
        var sentData = {password: $(this).val()};
        $.get('ajaxPasswordCheck', sentData, function(receivedData){
            
            switch (receivedData)
            {
                case "":
                {
                    var3 = true;
                    $('#info3').text('');
                    showButton();
                }
                break;
                
                case "weak password":
                {
                    var3 = false;
                    $('#info3').text('Password must be atleast 8 characters long').removeClass('blueColor').addClass('redColor');
                    hideButton();
                }
                break;
                
                case "invalid entry":
                {
                    var3 = false;
                    $('#info3').text('Password has invalid character(s)').removeClass('blueColor').addClass('redColor');
                    hideButton();
                }
                break;
            }
        });
        event.stopPropagation();
    });
    
    $('#resetPassword').focusout(function (event){
        var sentData = {password: $(this).val()};
        $.get('ajaxPasswordCheck', sentData, function(receivedData){
            
            switch (receivedData)
            {
                case "":
                {
                    $('#info3').text('');
                    $('#reset').removeClass('hidden');
                }
                break;
                
                case "weak password":
                {
                    $('#info3').text('Password must be atleast 8 characters long').removeClass('blueColor').addClass('redColor');
                    $('#reset').addClass('hidden');
                }
                break;
                
                case "invalid entry":
                {
                    $('#info3').text('Password has invalid character(s)').removeClass('blueColor').addClass('redColor');
                    $('#reset').addClass('hidden');
                }
                break;
            }
        });
        event.stopPropagation();
    });
    
    $('#radioDiv input[type = radio]').each(function (){
       $(this).click(function (event){
            if($(this).val() !== 'memelogic')
            {
               $('#coverImgOptional').fadeOut(500);//.addClass('hidden');
               $('#coverimg').val('');
            }
            else
            {
                $('#coverImgOptional').fadeIn(500).removeClass('hidden');
            }
            event.stopPropagation();
       });
    });
   
   function checkSession()
    {
        if($('#sessionOn').text() !== 'no session')
        {
            return true;
        }
    }
    
    function checkRank()
    {
        var postrank = $('#postRank').text().replace("PR: ", "");
        if(parseInt($('#userRank').text()) >= parseInt(postrank))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    $('#commentOnPost').click(function (){
        $('div.subcommentsOnTheGo').addClass('hidden');
        $('div.dynamicQuote').addClass('hidden');
        $('div.dynamicEditComment').addClass('hidden');
        
        $('div.smileyBlockSubCommentOnTheGo').addClass('hidden');
        $('div.smileyBlockDynamicQuote').addClass('hidden');
        $('div.smileyBlockDynamicEditComment').addClass('hidden');
        $('div.smileyBlockDynamicEditSubComment').addClass('hidden');
        $('#smileyBlock').addClass('hidden');
        /*if($('#smileyBlock').is(':visible'))
        {
            $('#smileyBlock').addClass('hidden');
        }*/
            
        if(checkSession())
        {
            if(checkRank())
            {
                $('#dynamicFormDiv').toggleClass('hidden');
                $('#kontent').val("");
                $('#info1').text("");
            }
            else
            {
                alert("Your rank is lesser than the post rank");
                //$('#info1').text("Your rank is lesser than the post rank").fadeToggle(500);
            }
        }
        else
        {
            alert("Please log in");
            //$('#info1').text("Please log in").fadeToggle(500);
        }
        return false;
    });
    
    $('#addimg').click(function (){
        $('#smileyBlock').addClass('hidden');
    });
    
    $('#addimg').change(function (){
        if(checkSession())
        {
            if(checkRank())
            {
                var kontent = $('#kontent').val();  //content of the textarea
                var fileName = $(this).val();
                var fakePath = 'C:\\fakepath\\';  //fakepath that comes with the filename
                if(fileName !== "")  //If a file was chosen at all
                {
                    fileName = fileName.replace(fakePath, "");  //Replace that fake path with nothing
                    var imgFile = this.files[0];  //Get the file itself
            
                    if(fileName.length <= 50)
                    {
                        if(fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".gif") 
                        || fileName.endsWith(".jpeg") || fileName.endsWith(".JPG") || fileName.endsWith(".PNG") 
                        || fileName.endsWith(".GIF") || fileName.endsWith(".JPEG") || fileName.endsWith(".webp") 
                        || fileName.endsWith(".WEBP"))
                        {
                            if(imgFile.size <= 4000000)
                            {
                                $('#info1').text('');
                                //var form = $('#dynamicForm');
                                var file = '<_'+ fileName +'_>';  //you know this already
            
                                //Now this is where Ajax is used to send the image to the server endpoint
                                disableButtons();
            
                                var myFormData = new FormData();  //creates a new formdata for uploading
                                myFormData.append("dynamicUpload", imgFile);  //Appends the file to the key
                        
                                $.ajax({
                                    enctype: 'multipart/form-data',
                                    type: 'POST',
                                    url: "ajaxDynamicFileUpload",
                                    data: myFormData,
                                    processData: false,
                                    contentType: false,
                                    cache: false,
                                    timeout: 600000,
                                    success: function (data) {
                                        if(data)
                                        {
                                            $('#kontent').val(kontent + file);  //Update the textarea
                                        }
                                        enableButtons();
                                    },
                                    error: function (e) {
                                        $('#kontent').val(kontent);  //Update the textarea
                                        enableButtons();
                                    }
                                });
                            }
                            else
                            {
                                alert("File size exceeded (4mb or less)");
                                //$('#info1').text('File size exceeded (4mb or less)');
                            }
                        }
                        else
                        {
                            alert("File format is not supported (suported formats: jpg, png, gif, webp)");
                            //$('#info1').text('File format is not supported (suported formats: jpg, png, gif, webp)');
                        }
                    }
                    else
                    {
                        alert("File name is too long, must be 50 characters of less");
                        //$('#info1').text('File name is too long, must be 50 characters of less');
                    }
                }
            }
            else
            {
                alert("Your rank is lesser than the post rank");
                //$('#info1').text("Unfortunately, your rank just dropped below the post rank");
            }
        }
        else
        {
            alert("Session expired. Please log in");
            //$('#info1').text("Session expired. Please log in");
        }
    });
    
    function enableButtons()
    {
        $('#addimg').prop("disabled", false);
        $('#dynamicSubmit').prop("disabled", false);
        $('#smiley').prop("disabled", false);
    }
    
    function disableButtons()
    {
        $('#addimg').prop("disabled", true);
        $('#dynamicSubmit').prop("disabled", true);
        $('#smiley').prop("disabled", true);
    }
    
    function enableButtonsX()
    {
        $('input.subCommentAddimg').prop("disabled", false);
        $('button.dynamicSubmitSubComment').prop("disabled", false);
        $('button.subCommentSmiley').prop("disabled", false);
    }
    
    function disableButtonsX()
    {
        $('input.subCommentAddimg').prop("disabled", true);
        $('button.subCommentSmiley').prop("disabled", true);
        $('button.dynamicSubmitSubComment').prop("disabled", true);
    }
    
    function enableButtonsY()
    {
        $('input.quoteCommentAddimg').prop("disabled", false);
        $('button.dynamicSubmitQuote').prop("disabled", false);
        $('button.quoteCommentSmiley').prop("disabled", false);
    }
    
    function disableButtonsY()
    {
        $('input.quoteCommentAddimg').prop("disabled", true);
        $('button.dynamicSubmitQuote').prop("disabled", true);
        $('button.quoteCommentSmiley').prop("disabled", true);
    }
    
    function enableButtonsZ()
    {
        $('input.editCommentAddimg').prop("disabled", false);
        $('button.editDynamicSubmitComment').prop("disabled", false);
        $('button.editCommentSmiley').prop("disabled", false);
    }
    
    function disableButtonsZ()
    {
        $('input.editCommentAddimg').prop("disabled", true);
        $('button.editDynamicSubmitComment').prop("disabled", true);
        $('button.editCommentSmiley').prop("disabled", true);
    }
    
    function enableButtonsP()
    {
        $('input.editSubCommentAddimg').prop("disabled", false);
        $('button.editDynamicSubmitSubComment').prop("disabled", false);
        $('button.editSubCommentSmiley').prop("disabled", false);
    }
    
    function disableButtonsP()
    {
        $('input.editSubCommentAddimg').prop("disabled", true);
        $('button.editDynamicSubmitSubComment').prop("disabled", true);
        $('button.editSubCommentSmiley').prop("disabled", true);
    }
    
    
    //Clear info on focus in of textarea
    $('#kontent').focusin(function (e){
        $('#info1').text("");
    });
    
    $('#dynamicSubmit').click(function (e){
        e.preventDefault();
        $('#smileyBlock').addClass('hidden');
        if(checkSession())
        {
            if(checkRank())
            {
                var kontent = $('#kontent').val();
                
                if(!kontent.match(/^\s*$/))
                {
                    if(checkTag(kontent))
                    {
                        if(checkScript(kontent))
                        {
                            disableButtons();
                    
                            var sentContent = {
                                content: $('#kontent').val(),
                                pos: $('#postid').text(),
                                title: $('#title').text(),
                                page: $('#commentPaginate').text(),
                                pg: $('#pagePaginate').text()
                            };
                    
                            $.ajax({
                                type: 'GET',
                                url: "ajaxDynamicComment",
                                data: "sent=" + encodeURIComponent(JSON.stringify(sentContent)),
                                processData: false,
                                contentType: "json",
                                cache: false,
                                timeout: 600000,
                                success: function () {
                                    $('#kontent').val("");  //Update the textarea
                                    $('#dynamicFormDiv').fadeOut(300);
                                    enableButtons();
                                    location.reload(true);
                                },
                                error: function (e) {
                                    $('#kontent').val(kontent);  //Update the textarea
                                    enableButtons();
                                }                    
                            });
                        }
                        else
                        {
                            alert("Malformed text, script not allowed");
                        }
                    }
                    else
                    {
                        alert("Malformed text, <_ must be followed by _>");
                    }
                }
            }
            else
            {
                alert("Your rank is lesser than the post rank");
                //$('#info1').text("Unfortunately, your rank just dropped below the post rank");
            }
        }
        else
        {
            alert("Session expired. Please log in");
            //$('#info1').text("Session expired. Please log in");
        }
    });
    
    
    function checkTag(content)
    {
        var test = false;
        
        if(content.includes("<_") && content.includes("_>"))
        {
            var countOpeningtag = content.lastIndexOf("<_"); //(content.match(/<_/g)).length;
            var countClosingTag = content.lastIndexOf("_>"); //(content.match(/_>/g)).length;
            
            if((content.match(/<_/g)).length !== (content.match(/_>/g)).length)
            {
                return test;
            }
            if(countClosingTag > countOpeningtag)
            {
                test = true;
            }
        }
        else if(!content.includes("<_") && !content.includes("_>"))
        {
            test = true;
        }
        
        return test;
    }
    
    function checkScript(content)
    {
        var test = false;
        if(!content.includes("<script>") && !content.includes("<script/>") && !content.includes("</script>"))
        {
            test = true;
        }
        return test;
    }
    
    $('#userContentAddImg').change(function (e){
        if(checkSession())
        {
            var kontent = $('#userContentTextArea').val();  //content of the textarea
            var fileName = $(this).val();
            var fakePath = 'C:\\fakepath\\';  //fakepath that comes with the filename
            if(fileName !== "")  //If a file was chosen at all
            {
                fileName = fileName.replace(fakePath, "");  //Replace that fake path with nothing
                var imgFile = this.files[0];  //Get the file itself
            
                if(fileName.length <= 50)
                {
                    if(fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".gif") 
                    || fileName.endsWith(".jpeg") || fileName.endsWith(".JPG") || fileName.endsWith(".PNG") 
                    || fileName.endsWith(".GIF") || fileName.endsWith(".JPEG") || fileName.endsWith(".webp") 
                    || fileName.endsWith(".WEBP"))
                    {
                        if(imgFile.size <= 4000000)
                        {
                            $('#info1').text('');
                            var file = '<_'+ fileName +'_>';  //you know this already
            
                            //Now this is where Ajax is used to send the image to the server endpoint
                            $('#userSubmitButton').prop("disabled", true);
                            $(this).prop("disabled", true);
            
                                var myFormData = new FormData();  //creates a new formdata for uploading
                                myFormData.append("userDynamicUpload", imgFile);  //Appends the file to the key
                        
                                $.ajax({
                                enctype: 'multipart/form-data',
                                type: 'POST',
                                url: "userAjaxDynamicFileUpload",
                                data: myFormData,
                                processData: false,
                                contentType: false,
                                cache: false,
                                timeout: 600000,
                                success: function (data) {
                                    if(data)
                                    {
                                        $('#userContentTextArea').val(kontent + file);  //Update the textarea
                                    }
                                    $('#userSubmitButton').prop("disabled", false);
                                    $('#userContentAddImg').prop("disabled", false);
                                },
                                error: function () {
                                    $('#userContentTextArea').val(kontent);  //Update the textarea
                                    $('#userSubmitButton').prop("disabled", false);
                                    $('#userContentAddImg').prop("disabled", false);
                                }
                            });
                        }
                        else
                        {
                            alert("File size exceeded (4mb or less)");
                            //$('#info1').text('File size exceeded (4mb or less)');
                        }
                    }
                    else
                    {
                        alert("File format is not supported (suported formats: jpg, png, gif, webp)");
                        //$('#info1').text('File format is not supported (suported formats: jpg, png, gif, webp)');
                    }
                }
                else
                {
                    alert("File name is too long, must be 50 characters of less");
                    //$('#info1').text('File name is too long, must be 50 characters of less');
                }
            }
        }
        else
        {
            alert("Session expired. Please log in");
            //come back here later to finalise it
            //$('#info1').text("Session expired. Please log in");
        }
    });
    
    $('a.commentsOnTheGo').each(function (index){
        $(this).click(function (ev){
            ev.preventDefault();
            $('#dynamicFormDiv').addClass('hidden');
            $('div.dynamicQuote').addClass('hidden');
            $('div.dynamicEditComment').addClass('hidden');
            $('div.dynamicEditSubComment').addClass('hidden');
            $('textarea.editSubCommentContent').val("");
            
            $('#smileyBlock').addClass('hidden');
            $('div.smileyBlockDynamicQuote').addClass('hidden');
            $('div.smileyBlockDynamicEditComment').addClass('hidden');
            $('div.smileyBlockDynamicEditSubComment').addClass('hidden');
            $('div.smileyBlockSubCommentOnTheGo').addClass('hidden');
            
            /*if($($('div.smileyBlockSubCommentOnTheGo')[index]).is(':visible'))
            {
                $($('div.smileyBlockSubCommentOnTheGo')[index]).addClass('hidden');
            }*/
            
            if(checkSession())
            {
                if(checkRank())
                {
                    $('form.dynamicSubComment').removeClass('hidden');
                    var selectSubComment = $('div.subcommentsOnTheGo')[index];
                    $(selectSubComment).toggleClass('hidden');
                    $('textarea.subCommentContent').val("");
            
                    $('div.subcommentsOnTheGo').each(function (e){
                        if(index !== e)
                        {
                            var otherSubcommentsOnTheGoDivs = $('div.subcommentsOnTheGo')[e];
                            if($(otherSubcommentsOnTheGoDivs).is(':visible'))
                            {
                                $(otherSubcommentsOnTheGoDivs).addClass('hidden');
                            }
                        }
                    });
                }
                else
                {
                    //$('#info1').text("Your rank is lesser than the post rank");
                    $($('div.subcommentsOnTheGo')[index]).toggleClass('hidden');
                    $($('form.dynamicSubComment')[index]).addClass('hidden');
                    
                    $('div.subcommentsOnTheGo').each(function (e){
                        if(index !== e)
                        {
                            var otherSubcommentsOnTheGoDivs = $('div.subcommentsOnTheGo')[e];
                            if($(otherSubcommentsOnTheGoDivs).is(':visible'))
                            {
                                $(otherSubcommentsOnTheGoDivs).addClass('hidden');
                            }
                        }
                    });
                }
            }
            else
            {
                //$('#info1').text("Session expired. Please log in");
                $($('div.subcommentsOnTheGo')[index]).toggleClass('hidden');
                $($('form.dynamicSubComment')[index]).addClass('hidden');
                    
                $('div.subcommentsOnTheGo').each(function (e){
                    if(index !== e)
                    {
                        var otherSubcommentsOnTheGoDivs = $('div.subcommentsOnTheGo')[e];
                        if($(otherSubcommentsOnTheGoDivs).is(':visible'))
                        {
                            $(otherSubcommentsOnTheGoDivs).addClass('hidden');
                        }
                    }
                });
            }
        });
    });
    
    $('input.subCommentAddimg').click(function(){
        $('div.smileyBlockSubCommentOnTheGo').addClass('hidden');
    });
    
    $('input.subCommentAddimg').each(function (index){
        $(this).change(function (){
            if(checkSession())
            {
                if(checkRank())
                {
                    var textArea = $($('textarea.subCommentContent')[index]);
                    var textAreaContent = textArea.val();
                    var fileName = $($('input.subCommentAddimg')[index]).val();
                    var fakePath = 'C:\\fakepath\\';
                    if(fileName !== "")
                    {
                        fileName = fileName.replace(fakePath, "");
                        var imgFile = $('input.subCommentAddimg')[index].files[0];
                        
                        if(fileName.length <= 50)
                        {
                            if(fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".gif") 
                            || fileName.endsWith(".jpeg") || fileName.endsWith(".JPG") || fileName.endsWith(".PNG") 
                            || fileName.endsWith(".GIF") || fileName.endsWith(".JPEG") || fileName.endsWith(".webp") 
                            || fileName.endsWith(".WEBP"))
                            {
                                if(imgFile.size <= 4000000)
                                {
                                    $('#info1').text('');
                                    var file = '<_'+ fileName +'_>';
                                    disableButtonsX();
                                    
                                    //textArea.val(textAreaContent + file);
            
                                    var myFormData = new FormData();
                                    myFormData.append("dynamicUpload", imgFile);
                        
                                    $.ajax({
                                        enctype: 'multipart/form-data',
                                        type: 'POST',
                                        url: "ajaxDynamicFileUpload",
                                        data: myFormData,
                                        processData: false,
                                        contentType: false,
                                        cache: false,
                                        timeout: 600000,
                                        success: function (data) {
                                            if(data)
                                            {
                                                textArea.val(textAreaContent + file);
                                            }
                                            enableButtonsX();
                                        },
                                        error: function () {
                                            textArea.val(textAreaContent);
                                            enableButtonsX();
                                        }
                                    });
                                }
                                else
                                {
                                    alert("File size exceeded (4mb or less)");
                                    //$('#info1').text('File size exceeded (4mb or less)');
                                }
                            }
                            else
                            {
                                alert("File format is not supported (suported formats: jpg, png, gif, webp)");
                                //$('#info1').text('File format is not supported (suported formats: jpg, png, gif, webp)');
                            }
                        }
                        else
                        {
                            alert("File name is too long, must be 50 characters of less");
                            //$('#info1').text('File name is too long, must be 50 characters of less');
                        }
                    }
                }
                else
                {
                    alert("Your rank is lesser than the post rank");
                    //$('#info1').text("Unfortunately, your rank just dropped below the post rank");
                }
            }
            else
            {
                alert("Session expired. Please log in");
                //$('#info1').text("Session expired. Please log in");
            }
        });
    });
    
    $('button.dynamicSubmitSubComment').each(function (index){
        $(this).click(function (ev){
            ev.preventDefault();
            $('div.smileyBlockSubCommentOnTheGo').addClass('hidden');
            if(checkSession())
            {
                if(checkRank())
                {
                    var textArea = $($('textarea.subCommentContent')[index]);
                    var textAreaContent = textArea.val();
                
                    if(!textAreaContent.match(/^\s*$/))
                    {
                        if(checkTag(textAreaContent))
                        {
                            if(checkScript(textAreaContent))
                            {
                                disableButtonsX();
                    
                                var sentContent = {
                                    content: textAreaContent,
                                    pos: $('#postid').text(),
                                    cid: $($('span.commentid')[index]).text(),
                                    title: $('#title').text(),
                                    page: $('#commentPaginate').text(),
                                    pg: $('#pagePaginate').text()
                                };
                    
                                $.ajax({
                                    type: 'GET',
                                    url: "user/ajaxSubCommentDynamicComment",
                                    data: "sent=" + encodeURIComponent(JSON.stringify(sentContent)),
                                    processData: false,
                                    contentType: "json",
                                    cache: false,
                                    timeout: 600000,
                                    success: function () {
                                        textArea.val("");
                                        $($('subcommentsOnTheGo')[index]).fadeOut(300);
                                        enableButtonsX();
                                        location.reload(true);
                                    },
                                    error: function () {
                                        textArea.val(textAreaContent);  //Update the textarea
                                        enableButtonsX();
                                        alert("error");
                                    }                    
                                });
                            }
                            else
                            {
                                alert("Malformed text, script not allowed");
                            }
                        }
                        else
                        {
                            alert("Malformed text, <_ must be followed by _>");
                        }
                    }
                }
                else
                {
                    alert("Your rank is lesser than the post rank");
                    //$('#info1').text("Unfortunately, your rank just dropped below the post rank");
                }
            }
            else
            {
                alert("Session expired. Please log in");
                //$('#info1').text("Session expired. Please log in");
            }
        });
    });
    
    $('input.quoteCommentAddimg').click(function(){
        $('div.smileyBlockDynamicQuote').addClass('hidden');
    });
    
    $('a.quotesOnTheGo').each(function (index){
        $(this).click(function (ev){
            ev.preventDefault();
            $('#dynamicFormDiv').addClass('hidden');
            $('div.subcommentsOnTheGo').addClass('hidden');
            $('div.dynamicEditComment').addClass('hidden');
            
            $('#smileyBlock').addClass('hidden');
            $('div.smileyBlockSubCommentOnTheGo').addClass('hidden');
            $('div.smileyBlockDynamicEditComment').addClass('hidden');
            $('div.smileyBlockDynamicEditSubComment').addClass('hidden');
            $('div.smileyBlockDynamicQuote').addClass('hidden');
            /*if($($('div.smileyBlockDynamicQuote')[index]).is(':visible'))
            {
                $($('div.smileyBlockDynamicQuote')[index]).addClass('hidden');
            }*/
            
            if(checkSession())
            {
                if(checkRank())
                {
                    var commentid = $($('span.commentid')[index]).text();
                    var selectQuote = $('div.dynamicQuote')[index];
                    $(selectQuote).toggleClass('hidden');
                    
                    //var commentForQuoting = $($('div.commentContentForQuoting')[index]).text();
                    disableButtonsY();
                    
                    $.ajax({
                        type: 'GET',
                        url: "user/getCommentToQuote",
                        data: "comment_id=" + encodeURIComponent(commentid),
                        processData: false,
                        contentType: "text",
                        cache: false,
                        timeout: 600000,
                        success: function (data) {
                            $($('textarea.quotedTextarea')[index]).val(data);
                            enableButtonsY();
                        },
                        error: function () {
                            $(selectQuote).addClass('hidden');
                        }
                    });
                    
                    $('div.dynamicQuote').each(function (e){
                        if(index !== e)
                        {
                            var otherQuoteOnTheGoDivs = $('div.dynamicQuote')[e];
                            if($(otherQuoteOnTheGoDivs).is(':visible'))
                            {
                                $(otherQuoteOnTheGoDivs).addClass('hidden');
                            }
                        }
                    });
                }
                else
                {
                    alert("Your rank is lesser than the post rank");
                    //$('#info1').text("Your rank is lesser than the post rank");
                }
            }
            else
            {
                alert("Session expired. Please log in");
                //$('#info1').text("Session expired. Please log in");
            }
        });
    });
    
    $('input.quoteCommentAddimg').each(function (index){
        $(this).change(function (){
            if(checkSession())
            {
                if(checkRank())
                {
                    var textArea = $($('textarea.dynamicQuoteContent1')[index]);
                    var textAreaContent = textArea.val();
                    if(textAreaContent.length < 646) // so 646 in Javascript is html's 700 character length
                    {
                        var fileName = $($('input.quoteCommentAddimg')[index]).val();
                        var fakePath = 'C:\\fakepath\\';
                        if(fileName !== "")
                        {
                            fileName = fileName.replace(fakePath, "");
                            var imgFile = $('input.quoteCommentAddimg')[index].files[0];
                        
                            if(fileName.length <= 50)
                            {
                                if(fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".gif") 
                                || fileName.endsWith(".jpeg") || fileName.endsWith(".JPG") || fileName.endsWith(".PNG") 
                                || fileName.endsWith(".GIF") || fileName.endsWith(".JPEG") || fileName.endsWith(".webp") 
                                || fileName.endsWith(".WEBP"))
                                {
                                    if(imgFile.size <= 4000000)
                                    {
                                        $('#info1').text('');
                                        var file = '<_'+ fileName +'_>';
                                    
                                        disableButtonsY();
                                        //textArea.val(textAreaContent + file);

                                        var myFormData = new FormData();
                                        myFormData.append("dynamicUpload", imgFile);
                        
                                        $.ajax({
                                            enctype: 'multipart/form-data',
                                            type: 'POST',
                                            url: "ajaxDynamicFileUpload",
                                            data: myFormData,
                                            processData: false,
                                            contentType: false,
                                            cache: false,
                                            timeout: 600000,
                                            success: function (data) {
                                                if(data)
                                                {
                                                    textArea.val(textAreaContent + file);
                                                }
                                                enableButtonsY();
                                            },
                                            error: function () {
                                                textArea.val(textAreaContent);
                                                enableButtonsY();
                                            }
                                        });
                                    }
                                    else
                                    {
                                        alert("File size exceeded (4mb or less)");
                                        //$('#info1').text('File size exceeded (4mb or less)');
                                    }
                                }
                                else
                                {
                                    alert("File format is not supported (suported formats: jpg, png, gif, webp)");
                                    //$('#info1').text('File format is not supported (suported formats: jpg, png, gif, webp)');
                                }
                            }
                            else
                            {
                                alert("File name is too long, must be 50 characters of less");
                                //$('#info1').text('File name is too long, must be 50 characters of less');
                            }
                        }
                    }
                    else
                    {
                        alert("Limit reached");
                    }
                }
                else
                {
                    alert("Your rank is lesser than the post rank");
                    //$('#info1').text("Unfortunately, your rank just dropped below the post rank");
                }
            }
            else
            {
                alert("Session expired. Please log in");
                //$('#info1').text("Session expired. Please log in");
            }
        });
    });
    
    $('button.dynamicSubmitQuote').each(function (index){
        $(this).click(function (ev){
            ev.preventDefault();
            $('div.smileyBlockDynamicQuote').addClass('hidden');
            if(checkSession())
            {
                if(checkRank())
                {
                    var quotingTextArea = $($('textarea.dynamicQuoteContent1')[index]);
                    var originalCommentBeforeQuoting = $($('div.commentContentForQuoting')[index]).text();
                    var textAreaQuotedContent = $($('textarea.quotedTextarea')[index]).val();
                    
                    var theQuotingText = quotingTextArea.val();
                    
                    if(!textAreaQuotedContent.match(/^\s*$/))
                    {
                        //if(originalCommentBeforeQuoting.includes(textAreaQuotedContent))
                        {
                            if(checkTag(textAreaQuotedContent))
                            {
                                if(checkScript(textAreaQuotedContent))
                                {
                                    if(!theQuotingText.match(/^\s*$/))
                                    {
                                        if(checkTag(theQuotingText))
                                        {
                                            if(checkScript(theQuotingText))
                                            {
                                                if(theQuotingText.length < 801)
                                                {
                                                    disableButtonsY();
                    
                                                    var sentContent = {
                                                        content: theQuotingText,
                                                        content2: textAreaQuotedContent, 
                                                        pos: $('#postid').text(),
                                                        cid: $($('span.commentid')[index]).text(),
                                                        title: $('#title').text(),
                                                        page: $('#commentPaginate').text(),
                                                        pg: $('#pagePaginate').text()
                                                    };
                    
                                                    $.ajax({
                                                        type: 'GET',
                                                        url: "user/ajaxSubCommentDynamicComment_",
                                                        data: "sent=" + encodeURIComponent(JSON.stringify(sentContent)),
                                                        processData: false,
                                                        contentType: "json",
                                                        cache: false,
                                                        timeout: 600000,
                                                        success: function () {
                                                            quotingTextArea.val("");
                                                            $($('div.dynamicQuote')[index]).fadeOut(300);
                                                            enableButtonsY();
                                                            location.reload(true);
                                                        },
                                                        error: function () {
                                                            quotingTextArea.val(theQuotingText);  //Update the textarea
                                                            enableButtonsY();
                                                            alert("Not posted");
                                                        }
                                                    });
                                                }
                                                else
                                                {
                                                    alert("Limit exceeded");
                                                }
                                            }
                                            else
                                            {
                                                alert("Malformed text, script not allowed");
                                            }
                                        }
                                        else
                                        {
                                            alert("Malformed text, <_ must be followed by _>");
                                        }
                                    }
                                    else
                                    {
                                        alert("Quote cannot be empty");
                                    }
                                }
                                else
                                {
                                    alert("Malformed text, script not allowed");
                                }
                            }
                            else
                            {
                                alert("Malformed text, <_ must be followed by _>");
                            }
                        }
                        /*else
                        {
                            alert("Quote is out of context");
                        }*/
                    }
                    else
                    {
                        alert("Quoted text cannot be empty");
                    }
                }
                else
                {
                    alert("Your rank is lesser than the post rank");
                    //$('#info1').text("Unfortunately, your rank just dropped below the post rank");
                }
            }
            else
            {
                alert("Session expired. Please log in");
                //$('#info1').text("Session expired. Please log in");
            }
        });
    });
    
    $('input.editCommentAddimg').click(function(){
        $('div.smileyBlockDynamicEditComment').addClass('hidden');
    });
    
    $('a.editComOnTheGo').each(function (index){
        $(this).click(function (ev){
            ev.preventDefault();
            $('#dynamicFormDiv').addClass('hidden');
            $('div.subcommentsOnTheGo').addClass('hidden');
            $('div.dynamicQuote').addClass('hidden');
            
            $('#smileyBlock').addClass('hidden');
            $('div.smileyBlockSubCommentOnTheGo').addClass('hidden');
            $('div.smileyBlockDynamicQuote').addClass('hidden');
            $('div.smileyBlockDynamicEditSubComment').addClass('hidden');
            $('div.smileyBlockDynamicEditComment').addClass('hidden');
            /*if($($('div.smileyBlockDynamicEditComment')[index]).is(':visible'))
            {
                $($('div.smileyBlockDynamicEditComment')[index]).addClass('hidden');
            }*/
            
            if(checkSession())
            {
                var commentid = $($('span.commentid')[index]).text();
                var selectQuote = $('div.dynamicEditComment')[index];
                $(selectQuote).toggleClass('hidden');
                
                disableButtonsZ();
                    
                $.ajax({
                    type: 'GET',
                    url: "user/getCommentToQuote",
                    data: "comment_id=" + encodeURIComponent(commentid),
                    processData: false,
                    contentType: "text",
                    cache: false,
                    timeout: 600000,
                    success: function (data) {
                        $($('textarea.editCommentContent')[index]).val(data);
                        enableButtonsZ();
                    },
                    error: function () {
                        $(selectQuote).addClass('hidden');
                    }
                });
                    
                $('div.dynamicEditComment').each(function (e){
                    if(index !== e)
                    {
                        var otherQuoteOnTheGoDivs = $('div.dynamicEditComment')[e];
                        if($(otherQuoteOnTheGoDivs).is(':visible'))
                        {
                            $(otherQuoteOnTheGoDivs).addClass('hidden');
                        }
                    }
                });
            }
            else
            {
                alert("Session expired. Please log in");
                //$('#info1').text("Session expired. Please log in");
            }
        });
    });
    
    $('input.editCommentAddimg').each(function (index){
        $(this).change(function (){
            if(checkSession())
            {
                var textArea = $($('textarea.editCommentContent')[index]);
                var textAreaContent = textArea.val();
                if(textAreaContent.length < 646) // so 646 in Javascript is html's 700 character length
                {
                    var fileName = $($('input.editCommentAddimg')[index]).val();
                    var fakePath = 'C:\\fakepath\\';
                    if(fileName !== "")
                    {
                        fileName = fileName.replace(fakePath, "");
                        var imgFile = $('input.editCommentAddimg')[index].files[0];
                        
                        if(fileName.length <= 50)
                        {
                            if(fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".gif") 
                            || fileName.endsWith(".jpeg") || fileName.endsWith(".JPG") || fileName.endsWith(".PNG") 
                            || fileName.endsWith(".GIF") || fileName.endsWith(".JPEG") || fileName.endsWith(".webp") 
                            || fileName.endsWith(".WEBP"))
                            {
                                if(imgFile.size <= 4000000)
                                {
                                    $('#info1').text('');
                                    var file = '<_'+ fileName +'_>';
                                    
                                    disableButtonsZ();

                                    var myFormData = new FormData();
                                    myFormData.append("dynamicUpload", imgFile);
                        
                                    $.ajax({
                                        enctype: 'multipart/form-data',
                                        type: 'POST',
                                        url: "ajaxDynamicFileUpload",
                                        data: myFormData,
                                        processData: false,
                                        contentType: false,
                                        cache: false,
                                        timeout: 600000,
                                        success: function (data) {
                                            if(data)
                                            {
                                                textArea.val(textAreaContent + file);
                                            }
                                            enableButtonsZ();
                                        },
                                        error: function () {
                                            textArea.val(textAreaContent);
                                            enableButtonsZ();
                                        }
                                    });
                                }
                                else
                                {
                                    alert("File size exceeded (4mb or less)");
                                    //$('#info1').text('File size exceeded (4mb or less)');
                                }
                            }
                            else
                            {
                                alert("File format is not supported (suported formats: jpg, png, gif, webp)");
                                //$('#info1').text('File format is not supported (suported formats: jpg, png, gif, webp)');
                            }
                        }
                        else
                        {
                            alert("File name is too long, must be 50 characters of less");
                            //$('#info1').text('File name is too long, must be 50 characters of less');
                        }
                    }
                }
                else
                {
                    alert("Limit reached");
                }
            }
            else
            {
                alert("Session expired. Please log in");
                //$('#info1').text("Session expired. Please log in");
            }
        });
    });
    
    $('button.editDynamicSubmitComment').each(function (index){
        $(this).click(function (ev){
            ev.preventDefault();
            $('div.smileyBlockDynamicEditComment').addClass('hidden');
            if(checkSession())
            {
                var editTextArea = $($('textarea.editCommentContent')[index]);
                var theEdit = editTextArea.val();
                    
                if(!theEdit.match(/^\s*$/))
                {
                    if(checkTag(theEdit))
                    {
                        if(checkScript(theEdit))
                        {
                            if(theEdit.length < 801)
                            {
                                disableButtonsZ();
                                var sentContent = {
                                    content: theEdit, 
                                    pos: $('#postid').text(),
                                    cid: $($('span.commentid')[index]).text(),
                                    title: $('#title').text(),
                                    page: $('#commentPaginate').text(),
                                    pg: $('#pagePaginate').text()
                                };
                    
                                $.ajax({
                                    type: 'GET',
                                    url: "user/ajaxSubCommentDynamicComment_editcomment",
                                    data: "sent=" + encodeURIComponent(JSON.stringify(sentContent)),
                                    processData: false,
                                    contentType: "json",
                                    cache: false,
                                    timeout: 600000,
                                    success: function () {
                                        editTextArea.val("");
                                        $($('div.dynamicEditComment')[index]).fadeOut(300);
                                        enableButtonsZ();
                                        location.reload(true);
                                    },
                                    error: function () {
                                        editTextArea.val(theEdit);  //Update the textarea
                                        enableButtonsZ();
                                        alert("Not posted");
                                    }
                                });
                            }
                            else
                            {
                                alert("Limit reached");
                            }
                        }
                        else
                        {
                            alert("Malformed text, script not allowed");
                        }
                    }
                    else
                    {
                        alert("Malformed text, <_ must be followed by _>");
                    }
                }
                else
                {
                    alert("Cannot be empty");
                }
            }
            else
            {
                alert("Session expired. Please log in");
                //$('#info1').text("Session expired. Please log in");
            }
        });
    });
    
    $('input.editSubCommentAddimg').click(function(){
        $('div.smileyBlockDynamicEditSubComment').addClass('hidden');
    });
    
    $('a.editSubComOnTheGo').each(function (index){
        $(this).click(function (ev){
            ev.preventDefault();
            //$('#dynamicFormDiv').addClass('hidden');
            $('form.dynamicSubComment').addClass('hidden');
            //$('div.smileyBlockDynamicEditSubComment').addClass('hidden');
            
            //$('div.dynamicQuote').addClass('hidden');
            $('#smileyBlock').addClass('hidden');
            $('div.smileyBlockSubCommentOnTheGo').addClass('hidden');
            $('div.smileyBlockDynamicQuote').addClass('hidden');
            $('div.smileyBlockDynamicEditComment').addClass('hidden');
            $('div.smileyBlockDynamicEditSubComment').addClass('hidden');
            
            if(checkSession())  //This is roundtrip
            {
                var subcommentid = $($('span.subcommentid')[index]).text();
                var selectQuote = $('div.dynamicEditSubComment')[index];
                $(selectQuote).toggleClass('hidden');
                
                disableButtonsP();
                
                $.ajax({
                    type: 'GET',
                    url: "user/getSubCommentToEdit",
                    data: "subcomment_id=" + encodeURIComponent(subcommentid),
                    processData: false,
                    contentType: "text",
                    cache: false,
                    timeout: 600000,
                    success: function (data) {
                        $($('textarea.editSubCommentContent')[index]).val(data);
                        enableButtonsP();
                    },
                    error: function () {
                        $(selectQuote).addClass('hidden');
                    }
                });
                
                $('div.dynamicEditSubComment').each(function (e){
                    if(index !== e)
                    {
                        var otherQuoteOnTheGoDivs = $('div.dynamicEditSubComment')[e];
                        if($(otherQuoteOnTheGoDivs).is(':visible'))
                        {
                            $($('textarea.editSubCommentContent')[e]).val('');
                            $(otherQuoteOnTheGoDivs).addClass('hidden');
                        }
                    }
                });
            }
            else
            {
                alert("Session expired. Please log in");
                //$('#info1').text("Session expired. Please log in");
            }
        });
    });
    
    $('input.editSubCommentAddimg').each(function (index){
        $(this).change(function (){
            if(checkSession())
            {
                var textArea = $($('textarea.editSubCommentContent')[index]);
                var textAreaContent = textArea.val();
                if(textAreaContent.length < 646) // so 646 in Javascript is html's 700 character length
                {
                    var fileName = $($('input.editSubCommentAddimg')[index]).val();
                    var fakePath = 'C:\\fakepath\\';
                    if(fileName !== "")
                    {
                        fileName = fileName.replace(fakePath, "");
                        var imgFile = $('input.editSubCommentAddimg')[index].files[0];
                        
                        if(fileName.length <= 50)
                        {
                            if(fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".gif") 
                            || fileName.endsWith(".jpeg") || fileName.endsWith(".JPG") || fileName.endsWith(".PNG") 
                            || fileName.endsWith(".GIF") || fileName.endsWith(".JPEG") || fileName.endsWith(".webp") 
                            || fileName.endsWith(".WEBP"))
                            {
                                if(imgFile.size <= 4000000)
                                {
                                    $('#info1').text('');
                                    var file = '<_'+ fileName +'_>';
                                    
                                    disableButtonsP();

                                    var myFormData = new FormData();
                                    myFormData.append("dynamicUpload", imgFile);
                        
                                    $.ajax({
                                        enctype: 'multipart/form-data',
                                        type: 'POST',
                                        url: "ajaxDynamicFileUpload",
                                        data: myFormData,
                                        processData: false,
                                        contentType: false,
                                        cache: false,
                                        timeout: 600000,
                                        success: function (data) {
                                            if(data)
                                            {
                                                textArea.val(textAreaContent + file);
                                            }
                                            enableButtonsP();
                                        },
                                        error: function () {
                                            textArea.val(textAreaContent);
                                            enableButtonsP();
                                        }
                                    });
                                }
                                else
                                {
                                    alert("File size exceeded (4mb or less)");
                                    //$('#info1').text('File size exceeded (4mb or less)');
                                }
                            }
                            else
                            {
                                alert("File format is not supported (suported formats: jpg, png, gif, webp)");
                                //$('#info1').text('File format is not supported (suported formats: jpg, png, gif, webp)');
                            }
                        }
                        else
                        {
                            alert("File name is too long, must be 50 characters of less");
                            //$('#info1').text('File name is too long, must be 50 characters of less');
                        }
                    }
                }
                else
                {
                    alert("Limit reached");
                }
            }
            else
            {
                alert("Session expired. Please log in");
                //$('#info1').text("Session expired. Please log in");
            }
        });
    });
    
    $('button.editDynamicSubmitSubComment').each(function (index){
        $(this).click(function (ev){
            ev.preventDefault();
            $('div.smileyBlockDynamicEditSubComment').addClass('hidden');
            if(checkSession())
            {
                var editTextArea = $($('textarea.editSubCommentContent')[index]);
                var theEdit = editTextArea.val();
                    
                if(!theEdit.match(/^\s*$/))
                {
                    if(checkTag(theEdit))
                    {
                        if(checkScript(theEdit))
                        {
                            if(theEdit.length < 801)
                            {
                                disableButtonsP();
                                var sentContent = {
                                    content: theEdit, 
                                    pos: $('#postid').text(),
                                    sid: $($('span.subcommentid')[index]).text(),
                                    title: $('#title').text(),
                                    page: $('#commentPaginate').text(),
                                    pg: $('#pagePaginate').text()
                                };
                    
                                $.ajax({
                                    type: 'GET',
                                    url: "user/ajaxDynamik_submit_edited_sub_comment_",
                                    data: "sent=" + encodeURIComponent(JSON.stringify(sentContent)),
                                    processData: false,
                                    contentType: "json",
                                    cache: false,
                                    timeout: 600000,
                                    success: function () {
                                        editTextArea.val("");
                                        $($('div.dynamicEditSubComment')[index]).fadeOut(300);
                                        enableButtonsP();
                                        location.reload(true);
                                    },
                                    error: function () {
                                        editTextArea.val(theEdit);  //Update the textarea
                                        enableButtonsP();
                                        alert("Not posted");
                                    }
                                });
                            }
                            else
                            {
                                alert("Limit reached");
                            }
                        }
                        else
                        {
                            alert("Malformed text, script not allowed");
                        }
                    }
                    else
                    {
                        alert("Malformed text, <_ must be followed by _>");
                    }
                }
                else
                {
                    alert("Cannot be empty");
                }
            }
            else
            {
                alert("Session expired. Please log in");
                //$('#info1').text("Session expired. Please log in");
            }
        });
    });
    
    $('button.editDynamicSubmitSubCommentExtraSubcommentPage').each(function (index){
        $(this).click(function (ev){
            ev.preventDefault();
            $('div.smileyBlockDynamicEditSubComment').addClass('hidden');
            if(checkSession())
            {
                var editTextArea = $($('textarea.editSubCommentContent')[index]);
                var theEdit = editTextArea.val();
                    
                if(!theEdit.match(/^\s*$/))
                {
                    if(checkTag(theEdit))
                    {
                        if(checkScript(theEdit))
                        {
                            if(theEdit.length < 801)
                            {
                                disableButtonsP();
                                var sentContent = {
                                    content: theEdit, 
                                    pos: $('#postid').text(),
                                    cid: $('#cid').text(),
                                    sid: $($('span.subcommentid')[index]).text(),
                                    title: $('#title').text(),
                                    p2: $('#p2').text(),
                                    pg: $('#pg').text(),
                                    page: $('#page').text()
                                };
                    
                                $.ajax({
                                    type: 'GET',
                                    url: "user/ajaxDynamik_submit_edited_sub_comment_2",
                                    data: "sent=" + encodeURIComponent(JSON.stringify(sentContent)),
                                    processData: false,
                                    contentType: "json",
                                    cache: false,
                                    timeout: 600000,
                                    success: function () {
                                        editTextArea.val("");
                                        $($('div.dynamicEditSubComment')[index]).fadeOut(300);
                                        enableButtonsP();
                                        location.reload(true);
                                    },
                                    error: function () {
                                        editTextArea.val(theEdit);  //Update the textarea
                                        enableButtonsP();
                                        alert("Not posted");
                                    }
                                });
                            }
                            else
                            {
                                alert("Limit reached");
                            }
                        }
                        else
                        {
                            alert("Malformed text, script not allowed");
                        }
                    }
                    else
                    {
                        alert("Malformed text, <_ must be followed by _>");
                    }
                }
                else
                {
                    alert("Cannot be empty");
                }
            }
            else
            {
                alert("Session expired. Please log in");
                //$('#info1').text("Session expired. Please log in");
            }
        });
    });
    
    /*
    function addDynamic1()
    {
        return '<form id="dynamicForm" action="" enctype="multipart/form-data" method="POST">'+
            '<div>'+
            '<textarea id="kontent" class="form-control" name="dynamicFormContent" placeholder="Comment" cols="20" rows="7" required="" maxlength="700"></textarea><br/></div>'+
            '<div class="uploadbtnwrapper">'+
            '<input id="addimg" class="btn btn-sm btn-default" name="dynamicFormFile" type="file" style="display: inline"/>'+
            '<button class="btn btn-sm btn-info" name="doesNothing"><span class="glyphicon glyphicon-cloud-upload" style="display: inline"></span> Attach Image</button>'+
            '<button id="smiley" class="btn btn-sm btn-primary" name="smiley" style="display: inline"><span class="glyphicon glyphicon-cloud-upload"></span> Smileys</button>'+
            '<button id="dynamicSubmit" class="btn btn-sm btn-success" style="display: inline"><span class=" glyphicon glyphicon-send"></span> Post</button>'+
        '</div></form>';
    }
    */
   /*
   $('#adminCommentOnPost').click(function (){
        //$('div.adminSubcommentsOnTheGo').addClass('hidden');
        //$('div.adminDynamicQuote').addClass('hidden');
        //$('div.adminDynamicEditComment').addClass('hidden');
            
        if(checkSession())
        {
            $('#adminDynamicFormDiv').toggleClass('hidden');
            //$('#kontent').val("");
            //$('#info1').text("");
        }
        else
        {
            $('#infox1').text("Please log in").fadeToggle(500);
        }
        return false;
    });
    */
   
   $('a.adminCommentsOnTheGo').each(function (index){
        $(this).click(function (ev){
            ev.preventDefault();
            
            var selectSubComment = $('div.adminSubcommentsOnTheGo')[index];
            $(selectSubComment).toggleClass('hidden');
            
            $('div.adminSubcommentsOnTheGo').each(function (e){
                if(index !== e)
                {
                    var otherSubcommentsOnTheGoDivs = $('div.adminSubcommentsOnTheGo')[e];
                    if($(otherSubcommentsOnTheGoDivs).is(':visible'))
                    {
                        $(otherSubcommentsOnTheGoDivs).addClass('hidden');
                    }
                }
            });
        });
    });
    
    //SMILEY_ZONE
    
    //dynamicFormDiv
    $('#smiley').click(function(e){
        e.preventDefault();
        $('#smileyBlock').toggleClass('hidden');
    });
    
    //subcommentsOnTheGo
    $('button.subCommentSmiley').each(function (index){
        $(this).click(function(e){
            e.preventDefault();
            $($('div.smileyBlockSubCommentOnTheGo')[index]).toggleClass('hidden');
            
            /*$('div.smileyBlockSubCommentOnTheGo').each(function(index2){
                if(index !== index2)
                {
                    $($('div.smileyBlockSubCommentOnTheGo')[index2]).addClass('hidden');
                }
            });*/
        });
    });
    
    //dynamicQuote
    $('button.quoteCommentSmiley').each(function (index){
        $(this).click(function(e){
            e.preventDefault();
            $($('div.smileyBlockDynamicQuote')[index]).toggleClass('hidden');
            
            /*$('div.smileyBlockDynamicQuote').each(function(index2){
                if(index !== index2)
                {
                    $($('div.smileyBlockDynamicQuote')[index2]).addClass('hidden');
                }
            });*/
        });
    });
    
    //dynamicEditComment
    $('button.editCommentSmiley').each(function (index){
        $(this).click(function(e){
            e.preventDefault();
            $($('div.smileyBlockDynamicEditComment')[index]).toggleClass('hidden');
            
            /*$('div.smileyBlockDynamicEditComment').each(function(index2){
                if(index !== index2)
                {
                    $($('div.smileyBlockDynamicEditComment')[index2]).addClass('hidden');
                }
            });*/
        });
    });
    
    //dynamicEditSubComment
    $('button.editSubCommentSmiley').each(function (index){
        $(this).click(function(e){
            e.preventDefault();
            $($('div.smileyBlockDynamicEditSubComment')[index]).toggleClass('hidden');
            
            /*$('div.smileyBlockDynamicEditSubComment').each(function(index2){
                if(index !== index2)
                {
                    $($('div.smileyBlockDynamicEditSubComment')[index2]).addClass('hidden');
                }
            });*/
        });
    });
}


/*
$('#smileyBlock').addClass('hidden');
        $('div.smileyBlockSubCommentOnTheGo').addClass('hidden');
        $('div.smileyBlockDynamicQuote').addClass('hidden');
        $('div.smileyBlockDynamicEditComment').addClass('hidden');
        $('div.smileyBlockDynamicEditSubComment').addClass('hidden');
        */