declare namespace output = "http://www.w3.org/2010/xslt-xquery-serialization";
declare option output:method "xml";
declare option output:indent "yes";

<preguntes>{
  let $posts := /posts/row[@PostTypeId="1"]
  let $sorted-posts := (
    for $q at $pos in $posts
    order by xs:integer($q/@Score) descending
    return $q
  )
  for $q at $pos in subsequence($sorted-posts, 1, 10)
  where $pos <= 100
  return 
    <pregunta>
      <titol>{$q/@Title}</titol>
      <body>{$q/@Body}</body>
      <tags>{$q/@Tags}</tags>
      <score>{$q/@Score}</score>
      {
        let $question_id := $q/@Id
        let $answers := /posts/row[@PostTypeId="2" and @ParentId=$question_id]
        let $max_score := max($answers/@Score)
        let $top_ans := $answers[@Score = $max_score][1]
        return
          <resposta>
            <id_res>{$top_ans/@Id}</id_res>
            <body_resp>{$top_ans/@Body}</body_resp>
            <score_resp>{$top_ans/@Score}</score_resp>
          </resposta>
      }
    </pregunta>
}</preguntes>