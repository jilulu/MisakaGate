<?php
header('Content-type:text/json');

class Convert {
	public $contents;
	public $json;
	private $table;
	private $anime;

	public function __construct($url) {
		$this->contents = file_get_contents($url);
		$this->json = array();
	}

    public function run() {
    	$this->format();
		$this->make_json();
		return json_encode($this->json);
    }

    private function format(){
    	$contents = str_replace("\n", '', $this->contents);

		$pattern = "#<tbody>(.*)</tbody>#";  
		preg_match($pattern, $contents, $tbody);
		$this->table = explode("</tr>", $tbody[1]);
		array_pop($this->table);

		$pattern = "#<script type=\"text/javascript\">var animeData =(.*);</script>#";  
		preg_match($pattern, $contents, $anime);
		$pattern = array("/{/", "/},/", "/\",/", "/:{/", "/:\"/");
		$replacement = array("{\"", "},\"", "\",\"", "\":{", "\":\"");
		$anime = preg_replace($pattern, $replacement, $anime[1]);
		$this->anime = json_decode($anime, true);
    }

    private function make_json(){
		foreach ($this->table as $subject) {
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
				'pic'		=> $this->anime[$data[1]]['pic'],
				'intro'		=> $this->anime[$data[1]]['intro'],
			);
			$this->json[] = $content;
		}
    }
}
//$url = "http://anime.2d-gate.org/__cache.html";
$url = $_GET['url'];
$obj = new Convert($url);
echo $obj->run();