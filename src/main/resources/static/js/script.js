console.log("this is script file")


const togglesidebar= () => {

    const sidebar = $(".sidebar");
  const content = $(".content");

  if (sidebar.is(":visible")) {
    // Hide sidebar
    sidebar.hide();
    content.css("margin-left", "0%");
  } else {
    // Show sidebar
    sidebar.show();
    content.css("margin-left", "20%");
  }
};