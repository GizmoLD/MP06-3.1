declare option output:method "xml";
declare option output:indent "yes";

<posts>{
  for $p in /posts/row[@PostTypeId='1']
  let $viewCount := xs:integer($p/@ViewCount)
  order by $viewCount descending
  return 
    <post>
      <title>{$p/@Title/string()}</title>
      <viewcount>{$p/@ViewCount/string()}</viewcount>
    </post>
}</posts>