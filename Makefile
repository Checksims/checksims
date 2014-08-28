LATEX=latexmk
LATEX_ROOT=doc
LATEX_SRC=$(LATEX_ROOT)/src
LATEX_DIST=$(LATEX_ROOT)/dist
LATEX_BUILD=$(LATEX_ROOT)/build
LATEX_BUILD_ARGS=-outdir=$(LATEX_DIST) -pdf

all: doc

doc: $(LATEX_DIST)/requirements.pdf mvaux

mvaux:
	mv $(LATEX_DIST)/*.aux $(LATEX_BUILD) || true
	mv $(LATEX_DIST)/*.log $(LATEX_BUILD) || true
	mv $(LATEX_DIST)/*.fls $(LATEX_BUILD) || true

$(LATEX_DIST)/requirements.pdf: $(LATEX_SRC)/requirements.ltx
	$(LATEX) $(LATEX_BUILD_ARGS) $(LATEX_SRC)/requirements.ltx

