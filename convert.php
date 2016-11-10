<?php
header('Content-type:text/json');
$url = $_GET['url'];
//$url = "http://2d-gate.org/thread-13075-1-1.html#.WCGideH5igQ";
//$url = "http://2d-gate.org/thread-13127-1-1.html#.WCK5WeF94sk";

$pattern = "#/thread-(.*)\.html#";
preg_match($pattern, $url, $thread);
$thread = $thread[1];  //13127-1-1

$contents = file_get_contents($url);
$contents = str_replace("\n", '', $contents);

$pattern = "#<title>(.*)</title>#";
preg_match($pattern, $contents, $title);
$title = $title[1];

$pattern = "#<div style=\"display:none\"(.*){jQuery\(this\)\.fadeIn\(300\)}}\)}\)</script>#";
preg_match($pattern, $contents, $content);
$content = $content[0];

$pattern = "#<div style=\"display:none\" id=\"(.*)\"><ul>(.*)</ul>#U";
preg_match($pattern, $content, $season);
$anime = array();
$anime['id'] = $season[1];
$anime['name'] = $title;
$anime['season'] = array();
$season = $season[2];

$pattern = "#<li><a href=\"/thread-" . $thread . "\.html\#(.*)\">(.*)</a></li>#U"; 
preg_match_all($pattern, $season, $seasons);
$sum = 0;
foreach ($seasons[1] as $i => $id) {
	$pattern = "#<div id=\"" . $id . "\"><div style=\"display:none\"(.*)</script></div>#U";
	$res = preg_match($pattern, $content, $tmp);
	$sum += $res;
}
if($sum == 0){ //multiseasons
	$seasons = array(array(), array($anime['id']), array('全一季'));
}

foreach ($seasons[1] as $i => $id) {
	$anime['season'][$i]['id'] = $id;
	$anime['season'][$i]['name'] = $seasons[2][$i];
	$anime['season'][$i]['episode'] = array();
	
	$pattern = "#id=\"" . $id . "\">(.*)</script>#U";
	preg_match($pattern, $content, $episodes);
	$episodes = $episodes[0];

	$pattern = "#<a href=\"/thread-" . $thread . "\.html\#(.*)\">(.*)</a>#U";  
	preg_match_all($pattern, $episodes, $links);
	foreach ($links[1] as $j => $id) {
		$anime['season'][$i]['episode'][$j]['id'] = $id;
		$anime['season'][$i]['episode'][$j]['name'] = $links[2][$j];
		
		$pattern = "#<div id=\"" . $id . "\">(.*)<span href=\"(.*)\" onclick#U";  
		preg_match($pattern, $episodes, $addr);
		$anime['season'][$i]['episode'][$j]['addr'] = $addr[2];
	}
}

echo json_encode($anime);