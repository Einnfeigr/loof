var loaded;
var checked = false;
var currentCheckbox;

$(window).on('load', function() {
	loaded = true;
});

$(document).ready(function() {
	setTimeout(function() {
		$('.loading').removeClass('animated');
		$('.loading').addClass('loaded');
		$('.loading.loaded').one("animationend webkitAnimationEnd oAnimationEnd MSAnimationEnd", function(){
			setTimeout(function() {
				$('.previewBackground').fadeOut(500, function() {
					$('.loading').hide();
				});
			}, 350);
		});	
	}, 200);
	

	if($('.status').length > 1) {
		$('.status.hidable').removeClass('status');
	}
	$('.previewBackground').removeClass('hidden');
	$('body').removeClass('preload');
	$('.dynamic').on('submit', function(e) {
	    e.preventDefault();
	    $('.status').hide();
	    changeStatusMessage("Отправка формы...");
		currentForm = $(this);
	    $.ajax({
			url: currentForm.attr('action'),
			type: currentForm.attr('method'),
			data: currentForm.serialize(),
			success: function(a) {
				var message = currentForm.attr('successMesage');
				if(message == null) {
					message = "Форма успешно отправлена";
				}
				if(currentForm.hasClass('reload')) {
					location.reload(true);
				}
				if(currentForm.hasClass('back')) {
					history.back();
				}
				if(currentForm.hasClass('redirect')) {
					window.location.replace("");
				}
				changeStatusMessage(message);
				currentForm.trigger("reset");	
			},
			error: function(a) {
				var message = $(this).attr('errorMesage');
				if(message == null) {
					message = "Ошибка при отправке формы <br> "
							+decodeURIComponent(a.getResponseHeader('error_message')).replaceAll("+", " ");
					console.log(a);
				}
				changeStatusMessage(message);
				$(this).trigger("reset");	
			}	
		});
	});
	$('.edHiddenElement').addClass('edHidden');
	$('.edCheckbox').on('click', function() {
		currentCheckbox = this;
		checked = !checked;
		if(checked) {
			$(this).addClass('checked');
			enableElements();
			showElements();
		} else {
			$(this).removeClass('checked');
			disableElements();
			hideElements();
		}
	});
});

function changeStatusMessage(message) {
    $('.status').fadeOut(200, function() {
        $('.status').empty();
        $('.status').append(message);
    });
    $('.status').fadeIn(500);
}

function enableElements() {
	$(currentCheckbox).parent().children('.edDisabled').addClass('edEnabled');
	$(currentCheckbox).parent().children('.edDisabled').removeClass('edDisabled');
}

function disableElements() {
	$(currentCheckbox).parent().children('.edEnabled').addClass('edDisabled');
	$(currentCheckbox).parent().children('.edEnabled').removeClass('edEnabled');
}

function showElements() {
	$(currentCheckbox).parent().children('.edHidden').addClass('edShown');
	$(currentCheckbox).parent().children('.edHidden').removeClass('edHidden');
}

function hideElements() {
	$(currentCheckbox).parent().children('.edShown').addClass('edHidden');
	$(currentCheckbox).parent().children('.edShown').removeClass('edShown');
}

function sendRequest(address, method, data, successMessage, errorMessage) {
	$.ajax({
		url: address,
		type: method,
		data: data,
		success: function(a) {
			changeStatusMessage(successMessage);
		},
		error: function(a) {
			changeStatusMessage(errorMessage);
		}
	});
}