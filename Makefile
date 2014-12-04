LATEX=latexmk
LATEX_ROOT=doc
LATEX_SRC=$(LATEX_ROOT)/src
LATEX_DIST=$(LATEX_ROOT)/dist
LATEX_BUILD=$(LATEX_ROOT)/build
LATEX_BUILD_ARGS=-outdir=$(LATEX_DIST) -pdf --shell-escape
BIBLIOGRAPHY=$(LATEX_SRC)/bibliography.bib

all: doc litreview methodology userguide devguide approach

doc: $(LATEX_DIST)/requirements.pdf mvaux

litreview: $(LATEX_DIST)/litreview.pdf mvaux

methodology: $(LATEX_DIST)/methodology.pdf mvaux

userguide: $(LATEX_DIST)/user_guide.pdf mvaux

devguide: $(LATEX_DIST)/developer_guide.pdf mvaux

approach: $(LATEX_DIST)/approach.pdf mvaux

mvaux:
	mv $(LATEX_DIST)/*.aux $(LATEX_BUILD) || true
	mv $(LATEX_DIST)/*.log $(LATEX_BUILD) || true
	mv $(LATEX_DIST)/*.fls $(LATEX_BUILD) || true

$(LATEX_DIST)/requirements.pdf: $(LATEX_SRC)/requirements.ltx
	$(LATEX) $(LATEX_BUILD_ARGS) $(LATEX_SRC)/requirements.ltx

$(LATEX_DIST)/litreview.pdf: $(LATEX_SRC)/litreview.ltx $(BIBLIOGRAPHY)
	$(LATEX) $(LATEX_BUILD_ARGS) $(LATEX_SRC)/litreview.ltx

$(LATEX_DIST)/methodology.pdf: $(LATEX_SRC)/methodology.ltx $(BIBLIOGRAPHY)
	$(LATEX) $(LATEX_BUILD_ARGS) $(LATEX_SRC)/methodology.ltx

$(LATEX_DIST)/user_guide.pdf: $(LATEX_SRC)/user_guide.ltx
	$(LATEX) $(LATEX_BUILD_ARGS) $(LATEX_SRC)/user_guide.ltx

$(LATEX_DIST)/developer_guide.pdf: $(LATEX_SRC)/developer_guide.ltx
	$(LATEX) $(LATEX_BUILD_ARGS) $(LATEX_SRC)/developer_guide.ltx

$(LATEX_DIST)/approach.pdf: $(LATEX_SRC)/approach.ltx $(LATEX_SRC)/architecture.pdf $(BIBLIOGRAPHY)
	$(LATEX) $(LATEX_BUILD_ARGS) $(LATEX_SRC)/approach.ltx

clean:
	rm -rf $(LATEX_DIST) $(LATEX_BUILD)
