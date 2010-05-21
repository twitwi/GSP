<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:xx="http://gnayacc.info/xslt/xxsl"
                version="2.0">
    <xsl:output method="text"/>
    
    <xsl:template match="/">
      <xsl:text>digraph Pipeline {
</xsl:text>
      <xsl:text>rankdir=LR;
</xsl:text>
      <xsl:text>node [color = black, shape = egg];
</xsl:text>
      <message xmlns="http://www.w3.org/1999/XSL/Transform">
         <xsl:text xmlns="">=== Doing preprocessing ===</xsl:text>
      </message>
      <xsl:variable name="preProcessed">
         <xsl:apply-templates select="//m | //module | //c | //connector"/>
      </xsl:variable>
      <message xmlns="http://www.w3.org/1999/XSL/Transform">
         <xsl:text xmlns="">=== Doing generation ===</xsl:text>
      </message>
      <message xmlns="http://www.w3.org/1999/XSL/Transform">
         <xsl:value-of xmlns="" select="$preProcessed"/>
      </message>
      <xsl:for-each select="$preProcessed/m">
         <xsl:text>subgraph "cluster_</xsl:text>
         <xsl:value-of select="@id"/>
         <xsl:text>" {
</xsl:text>
         <xsl:text>"node_</xsl:text>
         <xsl:value-of select="@id"/>
         <xsl:text>" [label="</xsl:text>
         <xsl:value-of select="@id"/>
         <xsl:text> [</xsl:text>
         <xsl:value-of select="@type"/>
         <xsl:text>]", style = filled, fillcolor = palegreen, shape = component] ;
</xsl:text>
         <xsl:variable name="m" select="."/>
         <xsl:for-each select="$preProcessed/c">
            <xsl:if test="$m/@id = @fromModule">
               <xsl:text>"</xsl:text>
               <xsl:value-of select="@fromModule"/>
               <xsl:text>@</xsl:text>
               <xsl:value-of select="@fromPort"/>
               <xsl:text>" [label="</xsl:text>
               <xsl:value-of select="@fromPort"/>
               <xsl:text>", style = filled, fillcolor = slategray2];
</xsl:text>
            </xsl:if>
            <xsl:if test="$m/@id = @toModule">
               <xsl:text>"</xsl:text>
               <xsl:value-of select="@toModule"/>
               <xsl:text>@</xsl:text>
               <xsl:value-of select="@toPort"/>
               <xsl:text>" [label="</xsl:text>
               <xsl:value-of select="@toPort"/>
               <xsl:text>", style = filled, fillcolor = plum];
</xsl:text>
            </xsl:if>
         </xsl:for-each>
         <xsl:text>}
</xsl:text>
      </xsl:for-each>
      <xsl:for-each select="$preProcessed/c">
         <xsl:text>"</xsl:text>
         <xsl:value-of select="@fromModule"/>
         <xsl:text>@</xsl:text>
         <xsl:value-of select="@fromPort"/>
         <xsl:text>" -&gt; "</xsl:text>
         <xsl:value-of select="@toModule"/>
         <xsl:text>@</xsl:text>
         <xsl:value-of select="@toPort"/>
         <xsl:text>" ;
</xsl:text>
      </xsl:for-each>
      <xsl:text>}
</xsl:text>
   </xsl:template>
   <xsl:template match="m | module">
      <xsl:copy>
         <xsl:copy-of select="@*"/>
         <xsl:apply-templates/>
      </xsl:copy>
   </xsl:template>
   <xsl:template match="c|connector">
      <xsl:choose>
         <xsl:when test="@chain">
            <xsl:variable name="elements" select="tokenize(@chain, '\s*-\s*')"/>
            <xsl:for-each select="$elements[position() != last()]">
               <xsl:variable name="i" select="position()"/>
               <xsl:variable name="last" select="last()"/>
               <xsl:variable name="e1" select="."/>
               <xsl:variable name="e2" select="$elements[1+$i]"/>
               <xsl:variable name="from"
                             select="tokenize(if ($i = 1) then concat('#',$e1) else $e1, '#')[position() &gt; 1]"/>
               <xsl:variable name="to" select="reverse(tokenize($e2, '#')[position() = (1,2)])"/>
               <message xmlns="http://www.w3.org/1999/XSL/Transform">
                  <xsl:value-of xmlns="" select="$from"/>
                  <xsl:text xmlns="">'     '</xsl:text>
                  <xsl:value-of xmlns="" select="$to"/>
               </message>
               <xsl:variable name="fromModule" select="if (count($from)=2) then $from[1] else $e1"/>
               <xsl:variable name="toModule" select="if (count($to)=2) then $to[1] else $e2"/>
               <xsl:variable name="fromPort"
                             select="if (count($from)=2 and not($from[2]='')) then $from[2] else 'output'"/>
               <xsl:variable name="toPort"
                             select="if (count($to[2])=2 and not($to[2]='')) then $to[2] else 'input'"/>
               <c fromModule="{$fromModule}" fromPort="{$fromPort}" toModule="{$toModule}"
                  toPort="{$toPort}"/>
            </xsl:for-each>
         </xsl:when>
         <xsl:otherwise>
            <xsl:variable name="from" select="tokenize(@from, '@')"/>
            <xsl:variable name="to" select="tokenize(@to, '@')"/>
            <xsl:variable name="fromPort" select="if ($from[2]) then $from[2] else 'output'"/>
            <xsl:variable name="toPort" select="if ($to[2]) then $to[2] else 'input'"/>
            <c fromModule="{$from[1]}" fromPort="{$fromPort}" toModule="{$to[1]}"
               toPort="{$toPort}"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
</xsl:stylesheet>