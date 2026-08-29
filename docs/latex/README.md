# Entregables LaTeX

Esta carpeta contiene las fuentes LaTeX para los documentos formales del proyecto.

- `informe-final.tex`: informe academico completo con la estructura solicitada.
- `manual-despliegue.tex`: manual breve de despliegue local, alineado con el entregable E5.
- `figures/`: capturas, diagramas y evidencias referenciadas por los documentos.

## Compilacion sugerida

Desde esta carpeta:

```bash
pdflatex informe-final.tex
pdflatex informe-final.tex
pdflatex manual-despliegue.tex
pdflatex manual-despliegue.tex
```

Los documentos usan `\IfFileExists` para que puedan compilar aunque alguna captura todavia no exista. Si una imagen falta, LaTeX muestra un recuadro con el nombre esperado.

