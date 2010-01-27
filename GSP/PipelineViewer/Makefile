
# This makefile is used for development only

f=test-pipeline.xml

view-pipeline.xsl: view-pipeline.xxsl 
	xxsl view-pipeline.xxsl view-pipeline.xsl

,,%.xml.dot: %.xml view-pipeline.xsl
	xslt view-pipeline.xsl $< $@

view: ,,$f.dot
	dotty ,,$f.dot

quick-test:
	make && touch ../c-pipeline.xml && ./view-pipeline.sh ../c-pipeline.xml

