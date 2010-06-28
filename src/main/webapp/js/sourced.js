(function($){
  var Sourced = (function() {
    return {
      recent: function(fn) {
        $.getJSON("api/recent?callback=?", fn);
      }
    };
  })();
  Sourced.recent(function(srcs) {
    $.each(srcs, function(i, src) {
      $(["<li><span class=\"org\">",
        src.org,
        "</span> sourced version <span class=\"version\">",
        src.version,
        "</span> of <span class=\"project\">",
        src.project, 
        "</span> <a href=\"",
        src.url,
        "\">view source</a></li>"
        ].join("")).appendTo("#source-list");
    });
  });
})(jQuery);