<%@ page import="java.util.*, com.jeantessier.dependency.*" %>
<html>

<head>
<title>Query <%= application.getInitParameter("name") %></title>
</head>

<!-- Reading the parameters and setting up the forms -->

<%
    String scope_includes = request.getParameter("scope-includes");
    if (scope_includes == null) {
	scope_includes = "//";
    }

    String scope_excludes = request.getParameter("scope-excludes");
    if (scope_excludes == null) {
	scope_excludes = "";
    }

    boolean package_scope = "on".equals(request.getParameter("package-scope"));
    if (request.getParameter("submit") == null) {
	package_scope = true;
    }

    boolean class_scope = "on".equals(request.getParameter("class-scope"));
    if (request.getParameter("submit") == null) {
	class_scope = true;
    }

    boolean feature_scope = "on".equals(request.getParameter("feature-scope"));
    if (request.getParameter("submit") == null) {
	feature_scope = true;
    }

    String filter_includes = request.getParameter("filter-includes");
    if (filter_includes == null) {
	filter_includes = "//";
    }

    String filter_excludes = request.getParameter("filter-excludes");
    if (filter_excludes == null) {
	filter_excludes = "";
    }

    boolean package_filter = "on".equals(request.getParameter("package-filter"));
    if (request.getParameter("submit") == null) {
	package_filter = true;
    }

    boolean class_filter = "on".equals(request.getParameter("class-filter"));
    if (request.getParameter("submit") == null) {
	class_filter = true;
    }

    boolean feature_filter = "on".equals(request.getParameter("feature-filter"));
    if (request.getParameter("submit") == null) {
	feature_filter = true;
    }

    boolean show_inbounds = "on".equals(request.getParameter("show-inbounds"));
    if (request.getParameter("submit") == null) {
	show_inbounds = true;
    }

    boolean show_outbounds = "on".equals(request.getParameter("show-outbounds"));
    if (request.getParameter("submit") == null) {
	show_outbounds = false;
    }

    boolean show_empty_nodes = "on".equals(request.getParameter("show-empty-nodes"));
    if (request.getParameter("submit") == null) {
	show_empty_nodes = false;
    }
%>

<body bgcolor="#ffffff">

<p>Dependency graph for <b><code><%= application.getInitParameter("name") %></code></b></p>

<form action="<%= request.getRequestURI() %>" method="post">

<table border="0" cellpadding="5"><tr><td colspan="2">

<table border="3" bgcolor="ccccff" cellpadding="4"><tr><td>

<table border="0">
    <tr>
	<td colspan="2">
	    <b>Select programming elements</b>
	</td>
    </tr>
    <tr>
	<td align="center" colspan="2">
	    <input type="checkbox" name="package-scope" <%= package_scope ? "checked" : "" %>>&nbsp;package
	    <input type="checkbox" name="class-scope" <%= class_scope ? "checked" : "" %>>&nbsp;class
	    <input type="checkbox" name="feature-scope" <%= feature_scope ? "checked" : "" %>>&nbsp;feature
	</td>
    </tr>
    <tr>
	<td>
	    including:
	</td>
	<td>
	    excluding:
	</td>
    </tr>
    <tr>
	<td>
	    <input type="text" name="scope-includes" value="<%= scope_includes %>">
	</td>
	<td>
	    <input type="text" name="scope-excludes" value="<%= scope_excludes %>">
	</td>
    </tr>
</table>

</td><td>

<table border="0">
    <tr>
	<td colspan="2">
	    <b>Show dependencies</b>
	</td>
    </tr>
    <tr>
	<td align="center" colspan="2">
	    <input type="checkbox" name="package-filter" <%= package_filter ? "checked" : "" %>>&nbsp;package
	    <input type="checkbox" name="class-filter" <%= class_filter ? "checked" : "" %>>&nbsp;class
	    <input type="checkbox" name="feature-filter" <%= feature_filter ? "checked" : "" %>>&nbsp;feature
	</td>
    </tr>
    <tr>
	<td>
	    including:
	</td>
	<td>
	    excluding:
	</td>
    </tr>
    <tr>
	<td>
	    <input type="text" name="filter-includes" value="<%= filter_includes %>">
	</td>
	<td>
	    <input type="text" name="filter-excludes" value="<%= filter_excludes %>">
	</td>
    </tr>
</table>

</td></tr><tr><td colspan="2" align="center">

Show dependencies
<input type="checkbox" name="show-inbounds" <%= show_inbounds ? "checked" : "" %>>&nbsp;to element
<input type="checkbox" name="show-outbounds" <%= show_outbounds ? "checked" : "" %>>&nbsp;from element
<input type="checkbox" name="show-empty-nodes" <%= show_empty_nodes ? "checked" : "" %>>&nbsp;(empty elements)

</td></tr></table>

</td></tr><tr>

<td align="left"><font size="-1">Use Perl regular expressions, <a target="_blank" href="http://depfind.sourceforge.net/Manual.html">see the manual</a></font></td>
<td align="right"><a href="advancedquery.jsp">advanced &gt;&gt;&gt;</a></td>

</tr><tr><td align="center" colspan="2">

<input type="submit" name="submit" value="Run Query"/>

</td></tr></table>

</form>

<hr/>

<%
    if (request.getParameter("submit") != null) {
	if (application.getAttribute("factory") != null) {
	    Date start = new Date();

	    SelectiveTraversalStrategy strategy = new SelectiveTraversalStrategy();
		
	    strategy.PackageScope(package_scope);
	    strategy.ClassScope(class_scope);
	    strategy.FeatureScope(feature_scope);
	    strategy.ScopeIncludes(scope_includes);
	    strategy.ScopeExcludes(scope_excludes);
	
	    strategy.PackageFilter(package_filter);
	    strategy.ClassFilter(class_filter);
	    strategy.FeatureFilter(feature_filter);
	    strategy.FilterIncludes(filter_includes);
	    strategy.FilterExcludes(filter_excludes);

	    GraphCopier dependencies_query = new GraphSummarizer(strategy);
	    if ("maximize".equalsIgnoreCase(application.getInitParameter("mode"))) {
		dependencies_query = new GraphCopier(strategy);
	    }
	
	    dependencies_query.TraverseNodes(((NodeFactory) application.getAttribute("factory")).Packages().values());

	    PrettyPrinter printer = new PrettyPrinter();

	    printer.ShowInbounds(show_inbounds);
	    printer.ShowOutbounds(show_outbounds);
	    printer.ShowEmptyNodes(show_empty_nodes);
		
	    printer.TraverseNodes(dependencies_query.ScopeFactory().Packages().values());

	    Date stop = new Date();

	    out.println();
%>

<pre><%= printer %></pre>

<p><%= (stop.getTime() - start.getTime()) / (double) 1000 %> secs.</p>

<%
	} else {
%>

<h3>No dependency graph available</h3>

<p>Please ask the webmaster to extract a dependency graph before you start placing queries.</p>

<%
	}
    }
%>

</body>

</html>
