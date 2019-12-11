(function ($) {

	"use strict"; 

	$(document).ready(function(){

		var indexTable = $('#indexTable');

		// Filter Status
		$('.projectReportContainer .projectReport .inner').click(function(e){

			// Toggle Filter Status
			var projectReport = $(this).parents('.projectReport');
			projectReport.toggleClass('active');

			// Check if Filtering Active and add/remove Class
			var projectReportContainer = $(this).parents('.projectReportContainer');
			if( projectReportContainer.find('.projectReport.active').length > 0 ){
				projectReportContainer.addClass('filteringActive');
				indexTable.addClass('filteringStatus');
			}else{
				projectReportContainer.removeClass('filteringActive');
				indexTable.removeClass('filteringStatus');
			}

			// show/hide filtered Rows
			var status = projectReport.data('status');
			indexTable.find('.tag.'+status).each(function(e){
				var row = $(this).parents('tr');
				if( projectReport.hasClass('active') ){
					row.addClass('showStatus');
				}else{
					row.removeClass('showStatus');
				}
			});

		});

		// Filter Assignee
		$('.assignees span').click(function(e){

			// Toggle Filter Assignee
			var assignee = $(this);
			assignee.toggleClass('active');

			// Check if Filtering Active and add/remove Class
			var assigneeContainer = $(this).parents('.assignees');
			if( assigneeContainer.find('.active').length > 0 ){
				indexTable.addClass('filteringAssignee');
			}else{
				indexTable.removeClass('filteringAssignee');
			}

			// show/hide filtered Rows
			var initials = assignee.data('initials');
			indexTable.find('td:contains("'+initials+'")').each(function(e){
				var row = $(this).parents('tr');
				if( assignee.hasClass('active') ){
					row.addClass('showAssignee');
				}else{
					row.removeClass('showAssignee');
				}
			});

		});

		// Counter & Percentages
		var l_todo = indexTable.find('.tag.todo').length;
		var l_inProgress = indexTable.find('.tag.inProgress').length;
		var l_designQs = indexTable.find('.tag.designQs').length;
		var l_done = indexTable.find('.tag.done').length;
		var l_feTaCheck = indexTable.find('.tag.feTaCheck').length;
		var l_custom = indexTable.find('.tag.custom').length;
		var l_all = l_todo + l_inProgress + l_designQs + l_done + l_feTaCheck + l_custom;

		var c_todo = $('.projectReportContainer .projectReport.todo .count');
		c_todo.find('.number').prepend( l_todo );
		c_todo.find('.percent').text( "~"+Math.round(""+l_todo*100/l_all)+"%" );

		var c_inProgress = $('.projectReportContainer .projectReport.inProgress .count');
		c_inProgress.find('.number').prepend( l_inProgress );
		c_inProgress.find('.percent').text( "~"+Math.round(l_inProgress*100/l_all)+"%" );

		var c_designQs = $('.projectReportContainer .projectReport.designQs .count');
		c_designQs.find('.number').prepend( l_designQs );
		c_designQs.find('.percent').text( "~"+Math.round(l_designQs*100/l_all)+"%" );

		var c_done = $('.projectReportContainer .projectReport.done .count');
		c_done.find('.number').prepend( l_done );
		c_done.find('.percent').text( "~"+Math.round(l_done*100/l_all)+"%" );

		var c_feTaCheck = $('.projectReportContainer .projectReport.feTaCheck .count');
		c_feTaCheck.find('.number').prepend( l_feTaCheck );
		c_feTaCheck.find('.percent').text( "~"+Math.round(l_feTaCheck*100/l_all)+"%" );

		var c_custom = $('.projectReportContainer .projectReport.custom .count');
		c_custom.find('.number').prepend( l_custom );
		c_custom.find('.percent').text( "~"+Math.round(l_custom*100/l_all)+"%" );


		// Mobile: Projectinformation & Important Links => Accordion functionality
		var projectOverall = $(".projectOverall");
		var importantLinks = projectOverall.children(".importantLinks");
		if($("html").hasClass("device-xs")) {
			importantLinks.children("h2").click(function() {
				importantLinks.children("ul").slideToggle();
				importantLinks.toggleClass("active");
			});
		}
	});


})(jQuery);