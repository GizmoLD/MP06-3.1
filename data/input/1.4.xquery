declare option output:method "xml";
declare option output:indent "yes";

let $tags := (
  for $tag in /tags/row
  order by xs:integer($tag/@Count) descending
  return $tag/@TagName
)[position() <= 10]

let $questions :=
  for $question in /posts/row[@PostTypeId="1"]
  where some $tag in $tags satisfies contains($question/@Tags, $tag) 
  order by xs:integer($question/@ViewCount) descending
  return 
    <question>
      <body>{$question/@Body}</body>
      <viewCount>{$question/@ViewCount}</viewCount>
      <tags>{$question/@Tags}</tags>
    </question>

return 
  <questions>{
    subsequence($questions, 1, 100)
  }</questions>  