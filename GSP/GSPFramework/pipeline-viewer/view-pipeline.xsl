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
      <xsl:apply-templates select="//m"/>
      <xsl:apply-templates select="//c"/>
      <xsl:text>}
</xsl:text>
   </xsl:template>
   <xsl:template match="m">
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
      <xsl:variable name="id" select="@id"/>
      <xsl:for-each select="//c">
         <xsl:variable name="split" select="tokenize(@from, '@')"/>
         <xsl:if test="$split[1] = $id">
            <xsl:text>"</xsl:text>
            <xsl:value-of select="@from"/>
            <xsl:text>" [label="</xsl:text>
            <xsl:value-of select="$split[2]"/>
            <xsl:text>", style = filled, fillcolor = slategray2];
</xsl:text>
         </xsl:if>
         <xsl:variable name="split" select="tokenize(@to, '@')"/>
         <xsl:if test="$split[1] = $id">
            <xsl:text>"</xsl:text>
            <xsl:value-of select="@to"/>
            <xsl:text>" [label="</xsl:text>
            <xsl:value-of select="$split[2]"/>
            <xsl:text>", style = filled, fillcolor = plum];
</xsl:text>
         </xsl:if>
      </xsl:for-each>
      <xsl:text>}
</xsl:text>
   </xsl:template>
   <xsl:template match="c">
      <xsl:text>"</xsl:text>
      <xsl:value-of select="@from"/>
      <xsl:text>" -&gt; "</xsl:text>
      <xsl:value-of select="@to"/>
      <xsl:text>" ;
</xsl:text>
   </xsl:template>
</xsl:stylesheet>