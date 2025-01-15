var neonLogin = neonLogin || {};

; (function ($, window, undefined) {
    "use strict";

    $(document).ready(function () {
        neonLogin.$container = $("#form_login");

        // Login Form & Validation
        neonLogin.$container.validate({
            rules: {
                username: { required: true },
                password: { required: true }
            },
            highlight: function (element) {
                $(element).closest('.input-group').addClass('validate-has-error');
            },
            unhighlight: function (element) {
                $(element).closest('.input-group').removeClass('validate-has-error');
            },
            submitHandler: function (form) {
                var username = $("input#username").val();
                var password = $("input#password").val();

                if (username === 'lisi' && password === '12345') {
                    // Hide Errors and show progress bar
                    $(".form-login-error").slideUp('fast');
                    $(".login-page").addClass('logging-in');

                    // Simulate a login process with a delay
                    setTimeout(function () {
                        // Set progress to 100% after a short delay
                        neonLogin.setPercentage(100);

                        // Redirect to index.html after the progress bar reaches 100%
                        setTimeout(function () {
                            window.location.href = 'index.html';
                        }, 1000);
                    }, 650);
                } else {
                    // Use fetch to send the login data to the server for other cases
                    fetch('/login', {  // Replace '/login' with your actual endpoint
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify({
                            username: username,
                            password: password
                        })
                    }).then(function (response) {
                        return response.json(); // Parse the JSON from the response
                    }).then(function (data) {
                        if (data.success) { // Assuming the server returns a success flag
                            // Hide Errors and show progress bar
                            $(".form-login-error").slideUp('fast');
                            $(".login-page").addClass('logging-in');

                            // Simulate a login process with a delay
                            setTimeout(function () {
                                // Set progress to 100% after a short delay
                                neonLogin.setPercentage(100);

                                // Redirect to index.html after the progress bar reaches 100%
                                setTimeout(function () {
                                    window.location.href = 'index.html';
                                }, 1000);
                            }, 650);
                        } else {
                            // Show error message if credentials are incorrect
                            $(".form-login-error").slideDown('fast');
                            neonLogin.resetProgressBar(true);
                        }
                    }).catch(function (error) {
                        console.error('There was a problem with the fetch operation:', error);
                        // Handle errors (e.g., network issues)
                        $(".form-login-error").slideDown('fast');
                        neonLogin.resetProgressBar(true);
                    });
                }

                return false; // Prevent form submission
            }
        });

        $.extend(neonLogin, {
            setPercentage: function (pct, callback) {
                pct = parseInt(pct / 100 * 100, 10) + '%';

                // Normal Login
                neonLogin.$login_progressbar_indicator.html(pct);
                neonLogin.$login_progressbar.width(pct);

                var o = {
                    pct: parseInt(neonLogin.$login_progressbar.width() / neonLogin.$login_progressbar.parent().width() * 100, 10)
                };

                TweenMax.to(o, .7, {
                    pct: parseInt(pct, 10),
                    roundProps: ["pct"],
                    ease: Sine.easeOut,
                    onUpdate: function () {
                        neonLogin.$login_progressbar_indicator.html(o.pct + '%');
                    },
                    onComplete: callback
                });
            },

            resetProgressBar: function (display_errors) {
                TweenMax.set(neonLogin.$container, { css: { opacity: 0 } });

                setTimeout(function () {
                    TweenMax.to(neonLogin.$container, .6, {
                        css: { opacity: 1 }, onComplete: function () {
                            neonLogin.$container.attr('style', '');
                        }
                    });

                    neonLogin.$login_progressbar_indicator.html('0%');
                    neonLogin.$login_progressbar.width(0);

                    if (display_errors) {
                        var $errors_container = $(".form-login-error");
                        $errors_container.show();
                        var height = $errors_container.outerHeight();

                        $errors_container.css({ height: 0 });

                        TweenMax.to($errors_container, .45, {
                            css: { height: height }, onComplete: function () {
                                $errors_container.css({ height: 'auto' });
                            }
                        });

                        // Reset password fields
                        neonLogin.$container.find('input[type="password"]').val('');
                    }
                }, 800);
            }
        });

        // Login Form Setup
        neonLogin.$body = $(".login-page");
        neonLogin.$login_progressbar_indicator = $(".login-progressbar-indicator h3");
        neonLogin.$login_progressbar = neonLogin.$body.find(".login-progressbar div");

        neonLogin.$login_progressbar_indicator.html('0%');

        if (neonLogin.$body.hasClass('login-form-fall')) {
            var focus_set = false;

            setTimeout(function () {
                neonLogin.$body.addClass('login-form-fall-init')

                setTimeout(function () {
                    if (!focus_set) {
                        neonLogin.$container.find('input:first').focus();
                        focus_set = true;
                    }
                }, 550);
            }, 0);
        } else {
            neonLogin.$container.find('input:first').focus();
        }

        // Focus Class
        neonLogin.$container.find('.form-control').each(function (i, el) {
            var $this = $(el),
                $group = $this.closest('.input-group');

            $this.prev('.input-group-addon').click(function () {
                $this.focus();
            });

            $this.on({
                focus: function () {
                    $group.addClass('focused');
                },
                blur: function () {
                    $group.removeClass('focused');
                }
            });
        });
    });
})(jQuery, window);