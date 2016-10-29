<?php
header('Content-type:text/json');
$url = "http://anime.2d-gate.org/__cache.html";
$contents = file_get_contents($url);
$contents = str_replace("\n", '', $contents);
$pattern = "#<tbody>(.*)</tbody>#";  
preg_match($pattern, $contents, $tbody);
$tr = explode("</tr>",$tbody[1]);
array_pop($tr);
$json = array();
foreach ($tr as $subject) {
	$pattern = "#<tr data-tid=\"(.*)\"><td>(.*)</td><td>(.*)</td><td>(.*)</td><td>(.*)</td><td>(.*)</td><td>(.*)</td><td class=\"(.*)\">(.*)</td><td>(.*)<p>(.*)</p></td><td>(.*)<p>(.*)</p></td><td>(.*)</td>#";  
	preg_match($pattern, $subject, $data);
	$content = array(
		'tid' => $data[1],
		'name' => $data[2],
		'subtitle' => $data[3],
		'resolution' => $data[4],
		'language' => $data[5],
		'episode' => $data[6],
		'year' => $data[7],
		'season' => $data[9],
		'release' => $data[10],
		'update' => $data[12],
	);
	$json[] = $content;
}
echo json_encode($json);