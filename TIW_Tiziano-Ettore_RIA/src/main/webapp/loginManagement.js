/**
 * Login management
 */

(function() { // avoid variables ending up in the global scope

  document.getElementById("loginbutton").addEventListener('click', (e) => {
    var form = e.target.closest("form");
    if (form.checkValidity()) {
      makeCall("POST", 'CheckLogin', e.target.closest("form"),
        function(x) {
          if (x.readyState == XMLHttpRequest.DONE) {
			try{
            	var message = JSON.parse(x.responseText);
			} catch (e){
				document.getElementById("errormessage").textContent = message;
			}
			if(x.status == 200){
				sessionStorage.setItem('user', JSON.stringify(message));
				if(message.role === ("docente")){
					window.location.href = "HomeDocente.html";
				}
				else{
					window.location.href = "HomeStudente.html";	
				}
			}
			else{
				document.getElementById("errormessage").textContent = message;
			}
          }
        }
      );
    } else {
    	 form.reportValidity();
    }
  });

})();






