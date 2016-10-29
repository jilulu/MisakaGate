<?php
header('Content-type:text/json');
$url = "http://anime.2d-gate.org/__cache.html";
$contents = file_get_contents($url);
$contents = str_replace("\n", '', $contents);

$pattern = "#<tbody>(.*)</tbody>#";  
preg_match($pattern, $contents, $tbody);
$tr = explode("</tr>", $tbody[1]);
array_pop($tr);

$pattern = "#<script type=\"text/javascript\">var animeData =(.*);</script>#";  
preg_match($pattern, $contents, $anime);
$pattern = array("/{/", "/},/", "/\",/", "/:{/", "/:\"/");
$replacement = array("{\"", "},\"", "\",\"", "\":{", "\":\"");
$anime = preg_replace($pattern, $replacement, $anime[1]);
$anime = json_decode($anime, true);

$json = array();
foreach ($tr as $subject) {
	$pattern = "#<tr data-tid=\"(.*)\"><td>(.*)</td><td>(.*)</td><td>(.*)</td><td>(.*)</td><td>(.*)</td><td>(.*)</td><td class=\"(.*)\">(.*)</td><td>(.*)<p>(.*)</p></td><td>(.*)<p>(.*)</p></td><td>(.*)</td>#";  
	preg_match($pattern, $subject, $data);

	$content = array(
		'tid' 		=> $data[1],
		'name' 		=> $data[2],
		'subtitle' 	=> $data[3],
		'resolution'	=> $data[4],
		'language' 	=> $data[5],
		'episode' 	=> $data[6],
		'year' 		=> $data[7],
		'season' 	=> $data[9],
		'release' 	=> $data[10].' '.$data[11],
		'update' 	=> $data[12].' '.$data[13],
		'pic'		=> $anime[$data[1]]['pic'],
		'intro'		=> $anime[$data[1]]['intro'],
	);
	$json[] = $content;
}
echo json_encode($json);
