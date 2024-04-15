declare option output:method "xml";
declare option output:indent "yes";

<tags>{
  for $t in /tags/row
  order by xs:integer($t/@Count) descending
  return 
    <tagInfo>
      <nom>{$t/@TagName/string()}</nom>
      <utilitzat>{$t/@Count/string()}</utilitzat>
    </tagInfo>
}</tags>