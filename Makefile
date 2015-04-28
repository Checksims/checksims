BUILD_ROOT=$(shell pwd)
LATEX=latexmk
LATEXMK_VERSION=$(latexmk -v | grep "Version" | sed s/^.*Version\ \\\(.*\\\).*$/\\1/g)
LATEX_ROOT=$(BUILD_ROOT)/doc
LATEX_DIST=$(LATEX_ROOT)/dist
LATEX_SRC=$(LATEX_ROOT)/src
LATEX_BUILD=$(LATEX_ROOT)/build
LATEX_BUILD_ARGS=-bibtex -pdf -outdir=$(LATEX_DIST) -cd
BIBLIOGRAPHY=$(LATEX_SRC)/bibliography.bib

all: docs jar

jar:
	mvn compile package

test:
	mvn compile test

docs: main userguide devguide

builddir:
	mkdir -p $(LATEX_ROOT)/dist

main: builddir
	$(LATEX) $(LATEX_BUILD_ARGS) $(LATEX_SRC)/main.ltx

userguide: builddir $(LATEX_DIST)/user_guide_only.pdf

devguide: builddir $(LATEX_DIST)/developer_guide_only.pdf

$(LATEX_DIST)/user_guide_only.pdf: $(LATEX_SRC)/user_guide_only.ltx $(LATEX_SRC)/user_guide.ltx
	$(LATEX) $(LATEX_BUILD_ARGS) $(LATEX_SRC)/user_guide_only.ltx

$(LATEX_DIST)/developer_guide_only.pdf: $(LATEX_SRC)/developer_guide_only.ltx $(LATEX_SRC)/developer_guide.ltx
	$(LATEX) $(LATEX_BUILD_ARGS) $(LATEX_SRC)/developer_guide_only.ltx

clean:
	rm -rf $(LATEX_ROOT)/dist
	mvn clean

