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
         <xsl:value-of select="(@m___label,@id)[1]"/>
         <xsl:text> [</xsl:text>
         <xsl:value-of select="@type"/>
         <xsl:text>]", style = filled, fillcolor = palegreen, shape = component] ;
</xsl:text>
         <xsl:variable name="m" select="."/>
         <xsl:for-each select="$preProcessed/c">
            <xsl:if test="$m/@id = @fromModule">
               <xsl:text>"</xsl:text>
               <xsl:value-of select="@fromModule"/>
               <xsl:text>#</xsl:text>
               <xsl:value-of select="@fromPort"/>
               <xsl:text>" [label="</xsl:text>
               <xsl:value-of select="@fromPort"/>
               <xsl:text>", style = filled, fillcolor = slategray2];
</xsl:text>
            </xsl:if>
            <xsl:if test="$m/@id = @toModule">
               <xsl:text>"</xsl:text>
               <xsl:value-of select="@toModule"/>
               <xsl:text>#</xsl:text>
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
         <xsl:text>#</xsl:text>
         <xsl:value-of select="@fromPort"/>
         <xsl:text>" -&gt; "</xsl:text>
         <xsl:value-of select="@toModule"/>
         <xsl:text>#</xsl:text>
         <xsl:value-of select="@toPort"/>
         <xsl:text>" ;
</xsl:text>
      </xsl:for-each>
      <xsl:text>}
</xsl:text>
   </xsl:template>
   <xsl:template match="m | module">
      <m>
         <copy-of xmlns="http://www.w3.org/1999/XSL/Transform" select="@*"/>
      </m>
   </xsl:template>
   <xsl:template name="preprocess-chain">
      <xsl:param name="chain" select="@chain"/>
      <xsl:variable name="elements" select="tokenize(@chain, '\s*-\s*')"/>
      <xsl:variable name="doc" select="/"/>
      <xsl:variable name="newchainid" select="generate-id(.)"/>
      <xsl:for-each select="$elements">
         <xsl:variable name="i" select="position()"/>
         <xsl:variable name="last" select="last()"/>
         <xsl:variable name="addIO">
            <xsl:variable name="split" select="tokenize(., '#')"/>
            <xsl:choose>
               <xsl:when test="$i=1">
                  <xsl:value-of select="if (count($split)=1) then concat('#',.,'#') else concat('#',.)"/>
               </xsl:when>
               <xsl:when test="$i=$last">
                  <xsl:value-of select="if (count($split)=1) then concat('#',.,'#') else concat(.,'#')"/>
               </xsl:when>
               <xsl:otherwise>
                  <xsl:value-of select="if (count($split)=1) then concat('#',.,'#') else ."/>
               </xsl:otherwise>
            </xsl:choose>
         </xsl:variable>
         <xsl:variable name="parts" select="tokenize($addIO, '#')"/>
         <xsl:variable name="id" select="concat($newchainid, '.', $i)"/>
         <xsl:for-each select="$doc//f[@id=$parts[2]]">
            <m m___label="{$parts[2]}">
               <copy-of xmlns="http://www.w3.org/1999/XSL/Transform" select="@*"/>
               <attribute xmlns="http://www.w3.org/1999/XSL/Transform" name="id" select="$id"/>
            </m>
            <message xmlns="http://www.w3.org/1999/XSL/Transform">
               <xsl:value-of xmlns="" select="concat(parts[1], '#', $id, '#', $parts[3])"/>
            </message>
            <e>
               <xsl:value-of select="concat(parts[1], '#', $id, '#', $parts[3])"/>
            </e>
         </xsl:for-each>
         <xsl:if test="count($doc//f[@id=$parts[2]]) = 0">
            <e>
               <xsl:value-of select="$addIO"/>
            </e>
         </xsl:if>
      </xsl:for-each>
   </xsl:template>
   <xsl:template match="c|connector">
      <xsl:choose>
         <xsl:when test="@chain">
            <xsl:variable name="c">
               <xsl:call-template name="preprocess-chain"/>
            </xsl:variable>
            <copy-of xmlns="http://www.w3.org/1999/XSL/Transform" select="$c/./m"/>
            <xsl:variable name="elements" select="($c/./e/text())"/>
            <xsl:for-each select="$elements[position() != last()]">
               <message xmlns="http://www.w3.org/1999/XSL/Transform">
                  <xsl:text xmlns="">E: </xsl:text>
                  <xsl:value-of xmlns="" select="."/>
               </message>
               <xsl:variable name="i" select="position()"/>
               <xsl:variable name="last" select="last()"/>
               <xsl:variable name="e1" select="."/>
               <xsl:variable name="e2" select="$elements[1+$i]"/>
               <xsl:variable name="from" select="tokenize($e1, '#')[position() &gt; 1]"/>
               <xsl:variable name="to" select="reverse(tokenize($e2, '#')[position() = (1,2)])"/>
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
            <message xmlns="http://www.w3.org/1999/XSL/Transform">
               <xsl:text xmlns="">ERR: found a </xsl:text>
               <xsl:value-of xmlns="" select="local-name()"/>
               <xsl:text xmlns=""> without chain attribute</xsl:text>
            </message>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
</xsl:stylesheet>