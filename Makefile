LATEX=latexmk
LATEX_ROOT=doc
LATEX_SRC=$(LATEX_ROOT)/src
LATEX_DIST=$(LATEX_ROOT)/dist
LATEX_BUILD=$(LATEX_ROOT)/build
LATEX_BUILD_ARGS=-bibtex -pdf -outdir=../dist -cd
BIBLIOGRAPHY=$(LATEX_SRC)/bibliography.bib

all: doc litreview annotebib

doc: $(LATEX_DIST)/requirements.pdf mvaux

litreview: $(LATEX_DIST)/litreview.pdf $(LATEX_SRC)/bibliography.bib 

annotebib: $(LATEX_DIST)/annotebib.pdf $(LATEX_SRC)/bibliography.bib

$(LATEX_DIST)/requirements.pdf: $(LATEX_SRC)/requirements.ltx
	$(LATEX) $(LATEX_BUILD_ARGS) $(LATEX_SRC)/requirements.ltx

$(LATEX_DIST)/litreview.pdf: $(LATEX_SRC)/litreview.ltx $(BIBLIOGRAPHY)
	$(LATEX) $(LATEX_BUILD_ARGS) $(LATEX_SRC)/litreview.ltx
	$(LATEX) $(LATEX_BUILD_ARGS) -c $(LATEX_SRC)/litreview.ltx

$(LATEX_DIST)/annotebib.pdf: $(LATEX_SRC)/annotebib.ltx $(BIBLIOGRAPHY)
	$(LATEX) $(LATEX_BUILD_ARGS) $(LATEX_SRC)/annotebib.ltx
	$(LATEX) $(LATEX_BUILD_ARGS) -c $(LATEX_SRC)/annotebib.ltx

