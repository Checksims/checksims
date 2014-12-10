LATEX=latexmk
LATEX_ROOT=doc
LATEX_SRC=$(LATEX_ROOT)/src
LATEX_DIST=$(LATEX_ROOT)/dist
LATEX_BUILD=$(LATEX_ROOT)/build
LATEX_BUILD_ARGS=-bibtex -pdf -outdir=../dist -cd
BIBLIOGRAPHY=$(LATEX_SRC)/bibliography.bib

all: builddir main userguide devguide

builddir:
	mkdir -p $(LATEX_DIST)

main: builddir $(LATEX_DIST)/main.pdf

userguide: builddir $(LATEX_DIST)/user_guide.pdf

devguide: builddir $(LATEX_DIST)/developer_guide.pdf

$(LATEX_DIST)/main.pdf: $(LATEX_SRC)/main.ltx $(LATEX_SRC)/approach.ltx $(LATEX_SRC)/methodology.ltx
	$(LATEX) $(LATEX_BUILD_ARGS) $(LATEX_SRC)/main.ltx

$(LATEX_DIST)/user_guide.pdf: $(LATEX_SRC)/user_guide.ltx
	$(LATEX) $(LATEX_BUILD_ARGS) $(LATEX_SRC)/user_guide.ltx

$(LATEX_DIST)/developer_guide.pdf: $(LATEX_SRC)/developer_guide.ltx
	$(LATEX) $(LATEX_BUILD_ARGS) $(LATEX_SRC)/developer_guide.ltx

clean:
	rm -rf $(LATEX_DIST) $(LATEX_BUILD)
