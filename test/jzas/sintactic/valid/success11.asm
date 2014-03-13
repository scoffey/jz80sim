                aseg
                org 100h

;REESTRICCIONES: NO FUNCIONA CON EL REGISTRO A
carga           macro r, zona
                local backup
                ld (backup), a
                ld ld a, r
                ld (zona), a
                ld a, backup
                jp backup+1
backup          ds 1
                endm

;FUNCIONA PARA TODOS LOS REGISTROS, CONSTANTES Y ROTULOS
descarga        macro zona, r
                local backup
                ld a, (backup)
                ld r, a
                ld a, (zona)
                ld a, (backup)
                jp backup + 1
backup          ds 1
                endm

                
inicio          carga b, 1234
                ld a, 'A'
                descarga 1234, a
                rst 38h
