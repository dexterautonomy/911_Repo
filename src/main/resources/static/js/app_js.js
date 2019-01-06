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
            }
            else
            {
                $('#coverImgOptional').fadeIn(500).removeClass('hidden');
            }
       });
    });
    
    /*
    $('#postRank').click(function(e){
        $('body.container').text($(this).text());
    });
    */
    
    /*
    $('#commentOnPost').hover(function (){
        if($('#sessionOn').text() !== 'no session')// && $('#userRank').text() !== null)
        {
            var postrank = $('#postRank').text().replace("PR: ", "");
            if(parseInt($('#userRank').text()) >= parseInt(postrank))
            {
                $(this).click(function (e){
                    $('#dynamicForm').fadeIn(1500).toggleClass('hidden');
                    return false;
                });
            }
            else
            {
                $(this).click(function (e){
                    return false;
                });
            }
        }
        else
        {
            window.alert('Please log in');
        }
    }, function (){
        
    });
    */
   
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
    
    $('#commentOnPost').click(function (e){
        if(checkSession())
        {
            if(checkRank())
            {
                $('#dynamicFormDiv').fadeToggle(300).removeClass('hidden');
                $('#kontent').val("");
                $('#info1').text("");
            }
            else
            {
                $('#info1').text("Your rank is lesser than the post rank").fadeToggle(500);
            }
        }
        else
        {
            $('#info1').text("Please log in").fadeToggle(500);
        }
        return false;
    });
    
    $('#addimg').change(function (e){
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
                        $(this).prop("disabled", true);  //Disable the fileupload button
                        $('#dynamicSubmit').prop("disabled", true);  //Disable the submit button
            
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
                                $('#addimg').prop("disabled", false);
                                $('#dynamicSubmit').prop("disabled", false);
                            },
                            error: function (e) {
                                $('#kontent').val(kontent);  //Update the textarea
                                $('#addimg').prop("disabled", false);
                                $('#dynamicSubmit').prop("disabled", false);
                            }
                        });
                        
                    }
                    else
                    {
                        $('#info1').text('File size exceeded (4mb or less)');
                    }
                }
                else
                {
                    $('#info1').text('File format is not supported (suported formats: jpg, png, gif, webp)');
                }
            }
            else
            {
                $('#info1').text('File name is too long, must be 50 characters of less');
            }
            
        }        
    });
    
    $('#kontent').focusin(function (e){
        $('#info1').text("");
    });
}